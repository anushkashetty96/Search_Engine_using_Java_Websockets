package controllers;

import models.*;
import org.apache.http.HttpStatus;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.User;
import org.junit.Test;

import static org.junit.Assert.*;
import static play.mvc.Results.ok;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import services.GithubService;

import java.util.*;
import java.util.concurrent.*;

import play.cache.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Test class for GithubController using mockito
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubControllerTest extends WithApplication {

    @Mock
    GithubService githubService;
    @Mock
    AsyncCacheApi cache;

    @InjectMocks
    GithubController githubController;


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void indexTest() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }



}

