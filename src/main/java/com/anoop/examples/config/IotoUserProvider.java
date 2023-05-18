package com.anoop.examples.config;

import com.anoop.examples.exceptions.ErrorCodes;
import com.anoop.examples.exceptions.InternalServerException;
import com.anoop.examples.exceptions.MSRuntimeException;
import com.anoop.examples.exceptions.NotFoundException;
import com.anoop.examples.model.IotoUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class IotoUserProvider implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return parameterIsIotoUser(methodParameter)
                && parameterTypeIsIotoUser(methodParameter);
    }

    private boolean parameterIsIotoUser(MethodParameter parameter) {
        return parameter.getParameterAnnotation(InjectIotoUser.class) != null;
    }

    private boolean parameterTypeIsIotoUser(MethodParameter methodParameter) {
        return methodParameter.getParameter().getType() == IotoUser.class;
    }

    @Override
    public IotoUser resolveArgument(MethodParameter methodParameter,
                                    ModelAndViewContainer modelAndViewContainer,
                                    NativeWebRequest nativeWebRequest,
                                    WebDataBinderFactory webDataBinderFactory) {
        if (supportsParameter(methodParameter)) {
            Principal principal = nativeWebRequest.getUserPrincipal();
            return getIotoActiveUser(principal);
        } else {
            return null;
        }
    }

    private IotoUser getIotoActiveUser(Principal principal) throws MSRuntimeException {
        try {
            if (principal instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken authentication = (JwtAuthenticationToken) principal;
                Jwt jwt = (Jwt) authentication.getPrincipal();
                return getIotoUser(jwt);
            }
            throw new NotFoundException(ErrorCodes.ACCOUNT_NOT_EXIST);
        } catch (Exception ex) {
            log.error("Error while creating IotoUser:", ex);
            throw new InternalServerException(ex);
        }
    }

    private IotoUser getIotoUser(Jwt jwt) {

        String userId = jwt.getClaimAsString("sub");
        String userName = jwt.getClaimAsString("preferred_username");
        String manufacturerId = jwt.getClaimAsString("manufacturer_id");
        String clientId = jwt.getClaimAsString("client_id");

        Map<String, Object> access = (Map<String, Object>) jwt.getClaims().get("realm_access");
        List<String> roles = (List<String>) access.get("roles");
        Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaims().get("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> clientResourceAccess = (Map<String, Object>) resourceAccess.get(clientId);
            List<String> clientRoles = (List<String>) clientResourceAccess.get("roles");
            roles.addAll(clientRoles);
        }

        String companyId = "";
        Map<String, Object> groups = (Map<String, Object>) jwt.getClaims().get("groups");
        List<String> groupIds = new ArrayList<>();
        for (String groupId : groups.keySet()) {
            groupIds.add(groupId);
        }
        if (groupIds.isEmpty() || groupIds.size() > 1) {
            throw new NotFoundException(ErrorCodes.COMPANY_NOT_EXIST);
        } else {
            companyId = groupIds.get(0);
        }

        boolean isCompanyUser = false;
        if (manufacturerId != null && !manufacturerId.equals(companyId)) {
            isCompanyUser = true;
        }

        return new IotoUser(userId, userName, clientId, companyId,
                isCompanyUser, roles, manufacturerId);
    }

}
