package org.apache.catalina.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.support.FileFinder;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.ContentType;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StatusCode;

public class RegisterController extends AbstractController {

    private static final String LOCATION = "Location";
    public static final String REGISTER_PAGE = "/register.html";
    private static final String INDEX_PAGE = "/index.html";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        if (request.hasBlankRegisterUserBody()) {
            final String body = "입력한 아이디, 비밀번호, 이메일에는 공백이 들어오면 안됩니다.";
            response.setStatusCode(StatusCode.BAD_REQUEST)
                .addHeader(CONTENT_TYPE, ContentType.HTML.getValue() + CHARSET_UTF_8)
                .addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length))
                .setBody(body);
            return;
        }
        final User registerUser = new User(
            request.getBody().getAccount(),
            request.getBody().getPassword(),
            request.getBody().getEmail());
        InMemoryUserRepository.save(registerUser);

        response.setStatusCode(StatusCode.FOUND)
            .addHeader(LOCATION, INDEX_PAGE);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (SessionManager.findSession(request.getAuthCookie()) == null) {
            final String body = FileFinder.find(REGISTER_PAGE);
            response.setStatusCode(StatusCode.OK)
                .addHeader(CONTENT_TYPE, ContentType.HTML.getValue() + CHARSET_UTF_8)
                .addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length))
                .setBody(body);
            return;
        }
        response.setStatusCode(StatusCode.FOUND)
            .addHeader(LOCATION, INDEX_PAGE);
    }
}
