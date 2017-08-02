package cc.blynk.core.http.rest;

import cc.blynk.core.http.UriTemplate;
import cc.blynk.core.http.annotation.*;
import cc.blynk.core.http.rest.params.Param;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.exceptions.WebException;
import cc.blynk.server.core.model.web.Role;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static cc.blynk.core.http.Response.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.12.15.
 */
public class Handler {

    private static final Logger log = LogManager.getLogger(Handler.class);

    public final UriTemplate uriTemplate;
    public final Method classMethod;
    public final Object handler;
    public final Param[] params;
    public final Role allowedRoleAccess;
    public HttpMethod httpMethod;

    public Handler(UriTemplate uriTemplate, Method method, Object handler) {
        this.uriTemplate = uriTemplate;
        this.classMethod = method;
        this.handler = handler;

        if (method.isAnnotationPresent(GET.class)) {
            this.httpMethod = HttpMethod.GET;
        }
        if (method.isAnnotationPresent(POST.class)) {
            this.httpMethod = HttpMethod.POST;
        }
        if (method.isAnnotationPresent(PUT.class)) {
            this.httpMethod = HttpMethod.PUT;
        }
        if (method.isAnnotationPresent(DELETE.class)) {
            this.httpMethod = HttpMethod.DELETE;
        }
        if (method.isAnnotationPresent(SuperAdmin.class)) {
            this.allowedRoleAccess = Role.SUPER_ADMIN;
        } else if (method.isAnnotationPresent(Admin.class)) {
            this.allowedRoleAccess = Role.ADMIN;
        } else if (method.isAnnotationPresent(Staff.class)) {
            this.allowedRoleAccess = Role.STAFF;
        } else {
            this.allowedRoleAccess = null;
        }

        this.params = new Param[method.getParameterCount()];
    }

    public Object[] fetchParams(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        Object[] res = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            res[i] = params[i].get(ctx, uriDecoder);
        }

        return res;
    }

    public FullHttpResponse invoke(Object[] params) {
        try {
            return (FullHttpResponse) classMethod.invoke(handler, params);
        } catch (InvocationTargetException ite) {
            Throwable te = ite.getTargetException();
            log.error("Error in invoked handler : ", te.getMessage());
            log.debug(te);
            return serverError(te.getMessage());
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof WebException) {
                if (cause instanceof ForbiddenWebException) {
                    return forbidden(cause.getMessage());
                } else {
                    return badRequest(cause.getMessage());
                }
            } else {
                log.error("Error invoking handler. Reason : {}.", e.getMessage());
                return serverError(e.getMessage());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Handler)) return false;

        Handler that = (Handler) o;

        if (uriTemplate != null ? !uriTemplate.equals(that.uriTemplate) : that.uriTemplate != null) return false;
        return !(httpMethod != null ? !httpMethod.equals(that.httpMethod) : that.httpMethod != null);

    }

    @Override
    public int hashCode() {
        int result = uriTemplate != null ? uriTemplate.hashCode() : 0;
        result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
        return result;
    }
}
