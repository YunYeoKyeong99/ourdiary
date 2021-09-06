package com.flower.ourdiary.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.ourdiary.util.ObjectMapperFactory;
import com.google.common.base.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
@SuppressWarnings("unused")
public class SwaggerResponseErrorPlugin implements OperationBuilderPlugin {

    private final static ObjectMapper objectMapper = ObjectMapperFactory.createWithDateFormat();

    private final AntPathMatcher antPathMatcher;

    public SwaggerResponseErrorPlugin() {
        antPathMatcher = new AntPathMatcher();
        antPathMatcher.setTrimTokens(false);
        antPathMatcher.setCaseSensitive(false);
    }

    @Override
    public void apply(OperationContext context) {
        Optional<RequestMapping> requestMappingOptional = context.findControllerAnnotation(RequestMapping.class);

        Optional<GetMapping> getMappingOptional = context.findAnnotation(GetMapping.class);
        Optional<PostMapping> postMappingOptional = context.findAnnotation(PostMapping.class);
        Optional<PutMapping> putMappingOptional = context.findAnnotation(PutMapping.class);
        Optional<DeleteMapping> deleteMappingOptional = context.findAnnotation(DeleteMapping.class);
        Optional<PatchMapping> patchMappingOptional = context.findAnnotation(PatchMapping.class);

        String path = requestMappingOptional.isPresent() ? requestMappingOptional.get().value()[0] : ""
                + (getMappingOptional.isPresent() ? getMappingOptional.get().path()[0]
                : postMappingOptional.isPresent() ? postMappingOptional.get().path()[0]
                : putMappingOptional.isPresent() ? putMappingOptional.get().path()[0]
                : deleteMappingOptional.isPresent() ? deleteMappingOptional.get().path()[0]
                : patchMappingOptional.isPresent() ? patchMappingOptional.get().path()[0] : "");

        boolean isPermitAll = false;
        for(String permitAllPath : Constant.PERMIT_ALL_PATHS) {
            if(antPathMatcher.match(permitAllPath, path)) {
                isPermitAll = true;
                break;
            }
        }

        Set<ResponseError> addedResponseErrorSet = new HashSet<>();

        if(!isPermitAll) {
            for (String authenticatedPath : Constant.AUTHENTICATED_PATHS) {
                if (antPathMatcher.match(authenticatedPath, path)) {
                    addedResponseErrorSet.add(ResponseError.UNAUTHORIZED);
                    break;
                }
            }

            for (String roleUserPath : Constant.ROLE_USER_PATHS) {
                if (antPathMatcher.match(roleUserPath, path)) {
                    addedResponseErrorSet.add(ResponseError.FORBIDDEN);
                    break;
                }
            }
        }

        Optional<SwaggerResponseError> swaggerResponseErrorOptional = context.findAnnotation(SwaggerResponseError.class);

        if(context.getParameters().size() >= 2) {
            context.getParameters().remove(1);
        }

        Set<ResponseMessage> responseMessageSet = getResponseMsgSetWhenErrorCodes(swaggerResponseErrorOptional, addedResponseErrorSet);

        if(!responseMessageSet.isEmpty())
            context.operationBuilder().responseMessages(responseMessageSet);
    }

    private Set<ResponseMessage> getResponseMsgSetWhenErrorCodes(Optional<SwaggerResponseError> swaggerResponseErrorOptional, Set<ResponseError> addedResponseErrorList) {
        Set<ResponseMessage> responseMessageSet = new HashSet<>();

        if(swaggerResponseErrorOptional.isPresent()) {
            SwaggerResponseError swaggerResponseError = swaggerResponseErrorOptional.get();

            Map<Integer, List<ResponseError>> map = new HashMap<>();
            for(ResponseError responseError : swaggerResponseError.value()) {
                map.computeIfAbsent(responseError.getHttpStatus().value(), ArrayList::new).add(responseError);
            }
            for(ResponseError responseError : addedResponseErrorList) {
                map.computeIfAbsent(responseError.getHttpStatus().value(), ArrayList::new).add(responseError);
            }

            for(Integer statusCode : map.keySet()) {
                List<ResponseError> responseErrorList = map.get(statusCode);
                responseErrorList.sort(Comparator.comparing(ResponseError::getHttpStatus));

                StringBuilder sb = new StringBuilder();
                for(ResponseError responseError : responseErrorList) {
                    try {
                        sb.append(objectMapper.writeValueAsString(responseError.exception()));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        sb.append("null");
                    }
                    sb.append('\n');
                }
                responseMessageSet.add( createResponseMessage(statusCode, sb.deleteCharAt(sb.length()-1).toString()) );
            }
        }

        return responseMessageSet;
    }

    private ResponseMessage createResponseMessage(int statusCode, String message) {
        return new ResponseMessage(statusCode, message, null, new HashMap<>(), new ArrayList<>());
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }
}
