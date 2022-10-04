package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import models.*;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Test class for GithubService using mockito
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class GithubServiceTest extends WithApplication {


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }


    @Mock
    RepositoryService repositoryService;
    @Mock
    IssueService issueService;
    @Mock
    UserService userService;
    @Mock
    Config config;
    @Mock
    CommitService commitService;

    @Mock
    GitHubClient mockClient;


    @InjectMocks
    GithubService githubServiceMock;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
    }

    /**
     * tests the service getRepositoryDetails
     * @author Ashwin Raghunath 40192120
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void getRepositoryDetailsTest() throws IOException, ExecutionException, InterruptedException {
        when(repositoryService.getRepository(any(String.class),any(String.class))).thenReturn(repository());
        CompletionStage<RepositoryDetails> repositoryDetails = githubServiceMock.getRepositoryDetails("userName", "MockRepoName");
        assertNotNull(repositoryDetails);
        RepositoryDetails repositoryDetails1 = repositoryDetails.toCompletableFuture().get();
        assertEquals(repositoryDetails1.getRepository().getName(),"MockRepoName");
    }

    /**
     * mock object for testing getRepositoryDetails
     * @author Ashwin Raghunath 40192120
     * @return Repository object contains mock values
     */
    private Repository repository()
    {
        Repository repository = new Repository();
        repository.setName("MockRepoName");
        return repository;
    }
    
    /**
     * tests the service getAllIssues
     * @author Anushka Shetty 40192371
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void getIssueWordLevelStatisticsTest() throws IOException, InterruptedException, ExecutionException {
    	when(issueService.getIssues(any(String.class), any(String.class), any())).thenReturn(issues());
    	CompletionStage<IssueWordStatistics> issueWordStatistics = githubServiceMock.getAllIssues("userName","MockRepoName");
    	assertNotNull(issueWordStatistics);
    	IssueWordStatistics issueWordStatisticsResult = issueWordStatistics.toCompletableFuture().get();
    	assertEquals(issueWordStatisticsResult.wordfrequency.size(),10);
    	assertEquals((int)issueWordStatisticsResult.getWordfrequency().get("null"),2);
    }

    /**
     * mock object for testing getAllIssues
     * @author Anushka Shetty 40192371
     * @return List<Issue> object contains mock values
     */
    private List<Issue> issues()
    { 	
    	List<Issue> issues = new ArrayList<Issue>();
    	Issue issue1 = new Issue();
    	issue1.setTitle("Null Pointer Exception");
    	Issue issue2 = new Issue();
    	issue2.setTitle("Null Reference");
    	Issue issue3 = new Issue();
    	issue3.setTitle("Index out of bound");
    	Issue issue4 = new Issue();
    	issue4.setTitle("Java Array");
    	issues.add(issue1);
    	issues.add(issue2);
    	issues.add(issue3);
    	issues.add(issue4);
    	return issues;
    }

    /**
     * tests the service getAllIssues
     * @author Sourav Uttam Sinha 40175660
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void getUserDetailsTest() throws IOException, ExecutionException, InterruptedException {
        when(userService.getUser(any(String.class))).thenReturn(user());
        CompletionStage<UserDetails> userDetails = githubServiceMock.getUserDetails("userName");
        assertNotNull(userDetails);
        UserDetails userDetails1 = userDetails.toCompletableFuture().get();
        assertEquals(userDetails1.getUser().getName(),"MockUserName");
    }

    /**
     * mock object for testing getRepositoryDetails
     * @author Sourav Uttam Sinha 40175660
     * @return User object contains mock values
     */
    private User user()
    {
        User user = new User();
        user.setName("MockUserName");
        return user;
    }

    /**
     * test for getRepositoriesByTopicTest function
     * @author Trusha Patel 40192614
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void getReposByTopicTest() throws IOException, InterruptedException, ExecutionException {
        List<SearchRepository> searchRepos = searchRepos();
        when(mockClient.getStream(any())).thenReturn(topicInputStream());
        when(repositoryService.searchRepositories(anyMap())).thenReturn(searchRepos);
        CompletionStage<SearchResults> searchedReposDetails = githubServiceMock.getReposByTopics("mocktopic");
        assertNotNull(searchedReposDetails);
        String actual = "";
        SearchResults details = searchedReposDetails.toCompletableFuture().get();
        for (SearchRepository searchRepository: details.getRepos()){
            actual = searchRepository.getName();
        }
        assertEquals("repo1",actual);
    }

    private List<SearchRepository> searchRepos() throws IOException {
        SearchRepository searchRepositoryMock1 = mock(SearchRepository.class);
        when(searchRepositoryMock1.getName()).thenReturn("repo1");
        when(searchRepositoryMock1.getOwner()).thenReturn("owner1");
        when(searchRepositoryMock1.getUrl()).thenReturn("https://github.com/mockuser/mockrepo");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012,11,12);
        when(searchRepositoryMock1.getPushedAt()).thenReturn(calendar.getTime());
        List<SearchRepository> list = new ArrayList<>();
        list.add(searchRepositoryMock1);
        return list;
    }
    /**
     * Testing for the topics fetched in the main search page
     * @author Trusha Patel 40192614
     * @throws IOException
     */

    @Test
    public void getTopicsTest() throws IOException{
        SearchRepository mocksearchRepository = mock(SearchRepository.class);
        GitHubRequest mockRequest = mock(GitHubRequest.class);
        when(mocksearchRepository.getUrl()).thenReturn("https://github.com/mockuser/mockrepo");
        when(mocksearchRepository.getName()).thenReturn("mockrepo");
        when(mocksearchRepository.getOwner()).thenReturn("mockuser");
        //The format of the return form is: https://github.com/CyC2018/CS-Notes
        when(mockClient.getStream(any())).thenReturn(topicInputStream());
        List<String> topics = githubServiceMock.getTopics(mocksearchRepository);
        List<String> expected = Arrays.asList("\"topic1\"", "\"topic2\"", "\"topic3\"");
        assertEquals(topics,expected);
    }

    /**
     * Mocks the topics for the fake API call
     * @return
     */

    private InputStream topicInputStream() {
        String mockTopics = "{" +
                "\"names\": [" +
                "\"topic1\"," +
                "\"topic2\"," +
                "\"topic3\"" +
                "]" +
                "}";
        InputStream stream = new ByteArrayInputStream(mockTopics.getBytes(StandardCharsets.UTF_8));
        return stream;
    }

    /**
     * Test case for searchResults method
     * @author Ashwin Raghunath 40192120
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void searchtest() throws IOException, ExecutionException, InterruptedException {
        List<SearchRepository> searchRepositoryList = searchRepositoryList();
        when(repositoryService.searchRepositories(anyString(),anyInt())).thenReturn(searchRepositoryList);
        when(mockClient.getStream(any())).thenReturn(topicInputStream());
        Http.RequestBuilder requestBuilder = new Http.RequestBuilder();
        CompletionStage<Map<String, List<UserRepositoryTopics>>> results = githubServiceMock.searchResults(requestBuilder.build(), "phrase");
        assertNotNull(results);
        Map<String, List<UserRepositoryTopics>> stringListMap = results.toCompletableFuture().get();
        assertEquals(1,stringListMap.size());

    }

    @Test
    public void searchResultsUsingActorstest() throws IOException, ExecutionException, InterruptedException {
        List<SearchRepository> searchRepositoryList = searchRepositoryList();
        when(repositoryService.searchRepositories(anyString(),anyInt())).thenReturn(searchRepositoryList);
        when(mockClient.getStream(any())).thenReturn(topicInputStream());
        CompletionStage<Map<String, List<UserRepositoryTopics>>> results = githubServiceMock.searchResultsUsingActors("phrase","session-key");
        assertNotNull(results);
        Map<String, List<UserRepositoryTopics>> stringListMap = results.toCompletableFuture().get();
        assertEquals(1,stringListMap.size());

    }

    /**
     * Mock object for testing search
     * @author Ashwin Raghunath 40192120
     * @return mock list
     */
    public List<SearchRepository> searchRepositoryList()
    {
        SearchRepository searchRepositoryMock1 = mock(SearchRepository.class);
        when(searchRepositoryMock1.getName()).thenReturn("name1");

        when(searchRepositoryMock1.getOwner()).thenReturn("owner1");

        when(searchRepositoryMock1.getUrl()).thenReturn("https://github.com/mockuser/mockrepo");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2012,11,12);

        when(searchRepositoryMock1.getCreatedAt()).thenReturn(calendar.getTime());

        List<SearchRepository> list = new ArrayList<>();
        list.add(searchRepositoryMock1);
        return list;
    }



    /**
     * Test Case for Commits Page
     * @author Anmol Malhotra 40201452
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void commitTest() throws IOException, ExecutionException, InterruptedException {
        when(repositoryService.getRepository(anyString(), anyString())).thenReturn(repository());
        List<RepositoryCommit> repositoryCommit = new ArrayList<>();
        RepositoryCommit repoCommit1 = new RepositoryCommit();
        repoCommit1.setSha("commitId");
        User user1 = new User();
        user1.setLogin("Anmol");
        repoCommit1.setAuthor(user1);
        repositoryCommit.add(repoCommit1);
        when(commitService.getCommits(any())).thenReturn(repositoryCommit);
        when(mockClient.getStream(any())).thenReturn(commitStatsInputStream());
        CompletionStage<CommitDetails> result = githubServiceMock.getCommitStatisticsForRepository("anmol", "repo");
        CommitDetails finalResult = result.toCompletableFuture().get();
        Assert.assertTrue(result.toCompletableFuture().isDone());
        Assert.assertEquals(1,finalResult.getMapOfUserAndCommits().size());
    }

    private InputStream commitStatsInputStream() {
        String mockCommitStatsData = "{\n" +
                "    \"committer\":\n" +
                "    {\n" +
                "        \"gists_url\": \"https://api.github.com/users/anmolmalhotra97/gists{/gist_id}\",\n" +
                "        \"repos_url\": \"https://api.github.com/users/anmolmalhotra97/repos\",\n" +
                "        \"following_url\": \"https://api.github.com/users/anmolmalhotra97/following{/other_user}\",\n" +
                "        \"starred_url\": \"https://api.github.com/users/anmolmalhotra97/starred{/owner}{/repo}\",\n" +
                "        \"login\": \"anmolmalhotra97\",\n" +
                "        \"followers_url\": \"https://api.github.com/users/anmolmalhotra97/followers\",\n" +
                "        \"type\": \"User\",\n" +
                "        \"url\": \"https://api.github.com/users/anmolmalhotra97\",\n" +
                "        \"subscriptions_url\": \"https://api.github.com/users/anmolmalhotra97/subscriptions\",\n" +
                "        \"received_events_url\": \"https://api.github.com/users/anmolmalhotra97/received_events\",\n" +
                "        \"avatar_url\": \"https://avatars.githubusercontent.com/u/26005821?v=4\",\n" +
                "        \"events_url\": \"https://api.github.com/users/anmolmalhotra97/events{/privacy}\",\n" +
                "        \"html_url\": \"https://github.com/anmolmalhotra97\",\n" +
                "        \"site_admin\": false,\n" +
                "        \"id\": 26005821,\n" +
                "        \"gravatar_id\": \"\",\n" +
                "        \"node_id\": \"MDQ6VXNlcjI2MDA1ODIx\",\n" +
                "        \"organizations_url\": \"https://api.github.com/users/anmolmalhotra97/orgs\"\n" +
                "    },\n" +
                "    \"stats\":\n" +
                "    {\n" +
                "        \"total\": 160,\n" +
                "        \"additions\": 160,\n" +
                "        \"deletions\": 0\n" +
                "    },\n" +
                "    \"author\":\n" +
                "    {\n" +
                "        \"gists_url\": \"https://api.github.com/users/anmolmalhotra97/gists{/gist_id}\",\n" +
                "        \"repos_url\": \"https://api.github.com/users/anmolmalhotra97/repos\",\n" +
                "        \"following_url\": \"https://api.github.com/users/anmolmalhotra97/following{/other_user}\",\n" +
                "        \"starred_url\": \"https://api.github.com/users/anmolmalhotra97/starred{/owner}{/repo}\",\n" +
                "        \"login\": \"anmolmalhotra97\",\n" +
                "        \"followers_url\": \"https://api.github.com/users/anmolmalhotra97/followers\",\n" +
                "        \"type\": \"User\",\n" +
                "        \"url\": \"https://api.github.com/users/anmolmalhotra97\",\n" +
                "        \"subscriptions_url\": \"https://api.github.com/users/anmolmalhotra97/subscriptions\",\n" +
                "        \"received_events_url\": \"https://api.github.com/users/anmolmalhotra97/received_events\",\n" +
                "        \"avatar_url\": \"https://avatars.githubusercontent.com/u/26005821?v=4\",\n" +
                "        \"events_url\": \"https://api.github.com/users/anmolmalhotra97/events{/privacy}\",\n" +
                "        \"html_url\": \"https://github.com/anmolmalhotra97\",\n" +
                "        \"site_admin\": false,\n" +
                "        \"id\": 26005821,\n" +
                "        \"gravatar_id\": \"\",\n" +
                "        \"node_id\": \"MDQ6VXNlcjI2MDA1ODIx\",\n" +
                "        \"organizations_url\": \"https://api.github.com/users/anmolmalhotra97/orgs\"\n" +
                "    },\n" +
                "    \"html_url\": \"https://github.com/anmolmalhotra97/Round-Robin-CPU-Scheduler/commit/6713ce9c113e6611a22e493d276f24fa5a13328f\",\n" +
                "    \"commit\":\n" +
                "    {\n" +
                "        \"comment_count\": 0,\n" +
                "        \"committer\":\n" +
                "        {\n" +
                "            \"date\": \"2018-12-11T04:30:50Z\",\n" +
                "            \"name\": \"anmolmalhotra97\",\n" +
                "            \"email\": \"anmolmalhotra97@gmail.com\"\n" +
                "        },\n" +
                "        \"author\":\n" +
                "        {\n" +
                "            \"date\": \"2018-12-11T04:30:50Z\",\n" +
                "            \"name\": \"anmolmalhotra97\",\n" +
                "            \"email\": \"anmolmalhotra97@gmail.com\"\n" +
                "        },\n" +
                "        \"tree\":\n" +
                "        {\n" +
                "            \"sha\": \"ebc676e02406031e83bd79f87e8ad8aa80e2983d\",\n" +
                "            \"url\": \"https://api.github.com/repos/anmolmalhotra97/Round-Robin-CPU-Scheduler/git/trees/ebc676e02406031e83bd79f87e8ad8aa80e2983d\"\n" +
                "        },\n" +
                "        \"message\": \"final commit for RR\",\n" +
                "        \"url\": \"https://api.github.com/repos/anmolmalhotra97/Round-Robin-CPU-Scheduler/git/commits/6713ce9c113e6611a22e493d276f24fa5a13328f\",\n" +
                "        \"verification\":\n" +
                "        {\n" +
                "            \"reason\": \"unsigned\",\n" +
                "            \"signature\": null,\n" +
                "            \"payload\": null,\n" +
                "            \"verified\": false\n" +
                "        }\n" +
                "    },\n" +
                "    \"comments_url\": \"https://api.github.com/repos/anmolmalhotra97/Round-Robin-CPU-Scheduler/commits/6713ce9c113e6611a22e493d276f24fa5a13328f/comments\",\n" +
                "    \"files\":\n" +
                "    [\n" +
                "        {\n" +
                "            \"patch\": \"@@ -0,0 +1,160 @@\\n+#include<iostream>\\r\\n+#include<stdio.h>\\r\\n+#include<string.h>\\r\\n+#include<cstdlib>\\r\\n+#include<ctime>\\r\\n+using namespace std;\\r\\n+struct node\\r\\n+{\\r\\n+    int pid;\\r\\n+    int ptime;\\r\\n+    struct node *next;\\r\\n+    struct node *prev;\\r\\n+};\\r\\n+static int i=1;\\r\\n+struct node *insert (struct node *head)\\r\\n+{\\r\\n+    int btime;\\r\\n+    node *ptr=head,*temp;\\r\\n+ temp=new node;\\r\\n+ temp->pid=i;\\r\\n+ i++;\\r\\n+ btime=rand()%100;\\r\\n+ temp->ptime=btime;\\r\\n+\\r\\n+ if(head==NULL)\\r\\n+ {\\r\\n+  head=temp;\\r\\n+  temp->next=head;\\r\\n+  temp->prev=head;\\r\\n+ }\\r\\n+ else{\\r\\n+\\r\\n+  while(ptr->next!=head)\\r\\n+  {\\r\\n+      ptr=ptr->next;\\r\\n+  }\\r\\n+  ptr->next=temp;\\r\\n+  temp->next=head;\\r\\n+  temp->prev=ptr;\\r\\n+  head->prev=temp;\\r\\n+ }\\r\\n+ return head;\\r\\n+};\\r\\n+void display(struct node *start)\\r\\n+{\\r\\n+    cout<<\\\"Pid: \\\\t Ptime:\\\\n\\\";\\r\\n+    struct node *ptr=start;\\r\\n+    while(ptr->next!=start)\\r\\n+    {\\r\\n+        cout<<ptr->pid<<\\\"\\\\t\\\"<<ptr->ptime<<endl;\\r\\n+        ptr=ptr->next;\\r\\n+    }\\r\\n+        cout<<ptr->pid<<\\\"\\\\t\\\"<<ptr->ptime<<endl;\\r\\n+}\\r\\n+struct node *deletion(struct node *head)\\r\\n+{\\r\\n+ node *temp=head;\\r\\n+ if(head==NULL)\\r\\n+ {cout<<\\\"No node in queue\\\"<<endl;\\r\\n+ return head;\\r\\n+ }\\r\\n+\\r\\n+ if(head->next==head)\\r\\n+ {\\r\\n+     head=NULL;\\r\\n+ }\\r\\n+ else\\r\\n+{\\r\\n+ node *ptr=head->next;\\r\\n+ ptr->prev=ptr->prev->prev;\\r\\n+ ptr->prev->next=ptr;\\r\\n+ head=ptr;\\r\\n+}\\r\\n+ delete(temp);\\r\\n+ return head;\\r\\n+}\\r\\n+struct node *roundrobin(struct node *head,int a[][2],int *m)\\r\\n+{\\r\\n+    int f=0;\\r\\n+    int totaltime=1500;\\r\\n+    static int c=0;\\r\\n+    int q;\\r\\n+    while(f!=1)\\r\\n+        {\\r\\n+\\r\\n+        int x=rand()%20;\\r\\n+        if(x==0 || x==1 )\\r\\n+        {\\r\\n+            f=0;\\r\\n+        }\\r\\n+        else\\r\\n+            {\\r\\n+                q=x;\\r\\n+                f=1;\\r\\n+            }\\r\\n+    }\\r\\n+    node* ptr=head;\\r\\n+    cout<<\\\"The time quantum is : \\\"<<q<<endl;\\r\\n+    int nptime = rand()%10;\\r\\n+    while(ptr!=NULL)\\r\\n+ {\\r\\n+  int t=0,id;\\r\\n+\\r\\n+  if(ptr->ptime<=q)\\r\\n+  {\\r\\n+      t=ptr->ptime;\\r\\n+      id=ptr->pid;\\r\\n+      ptr->ptime=0;\\r\\n+      //cout<<ptr->pid<<\\\"\\\\t\\\"<<ptr->ptime<<endl;\\r\\n+      ptr=deletion(ptr);\\r\\n+  }\\r\\n+  else\\r\\n+  {\\r\\n+       ptr->ptime=ptr->ptime-q;\\r\\n+       t=q;\\r\\n+       id=ptr->pid;\\r\\n+       //cout<<ptr->pid<<\\\"\\\\t\\\"<<ptr->ptime<<endl;\\r\\n+       ptr=ptr->next;\\r\\n+  }\\r\\n+  a[c][0]=id;\\r\\n+  if(c>0)\\r\\n+  {\\r\\n+      t+=a[c-1][1];\\r\\n+  }\\r\\n+  a[c][1]=t;\\r\\n+  c++;\\r\\n+  if(t>=totaltime)\\r\\n+      break;\\r\\n+  if(t>=nptime)\\r\\n+  {\\r\\n+      ptr=insert(ptr);\\r\\n+      nptime=rand()%(t+100);\\r\\n+  }\\r\\n+}\\r\\n+*m=c;\\r\\n+ return ptr;\\r\\n+};\\r\\n+int main()\\r\\n+{\\r\\n+    srand(time(NULL));\\r\\n+    int gc[1000][2]={0},m;\\r\\n+    struct node *start=NULL;\\r\\n+    int t=3;\\r\\n+\\r\\n+    while(t--)\\r\\n+    {\\r\\n+        start=insert(start);\\r\\n+    }\\r\\n+    display(start);\\r\\n+    start=roundrobin(start,gc,&m);\\r\\n+   // start=deletion(start);\\r\\n+\\r\\n+    cout<<\\\"------------ GANT CHART -----------\\\"<<endl<<endl;\\r\\n+for(i=0;i<m;i++)\\r\\n+ {\\r\\n+     cout<<gc[i][0]<<\\\" \\\"<<gc[i][1]<<endl;\\r\\n+ }\\r\\n+    return 0;\\r\\n+}\\r\\n+\\r\",\n" +
                "            \"filename\": \"round robin.cpp\",\n" +
                "            \"additions\": 160,\n" +
                "            \"deletions\": 0,\n" +
                "            \"changes\": 160,\n" +
                "            \"sha\": \"3654eb385c2606949f7d1d9dacb1a1b1e6f9ca8e\",\n" +
                "            \"blob_url\": \"https://github.com/anmolmalhotra97/Round-Robin-CPU-Scheduler/blob/6713ce9c113e6611a22e493d276f24fa5a13328f/round%20robin.cpp\",\n" +
                "            \"raw_url\": \"https://github.com/anmolmalhotra97/Round-Robin-CPU-Scheduler/raw/6713ce9c113e6611a22e493d276f24fa5a13328f/round%20robin.cpp\",\n" +
                "            \"status\": \"added\",\n" +
                "            \"contents_url\": \"https://api.github.com/repos/anmolmalhotra97/Round-Robin-CPU-Scheduler/contents/round%20robin.cpp?ref=6713ce9c113e6611a22e493d276f24fa5a13328f\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"sha\": \"6713ce9c113e6611a22e493d276f24fa5a13328f\",\n" +
                "    \"url\": \"https://api.github.com/repos/anmolmalhotra97/Round-Robin-CPU-Scheduler/commits/6713ce9c113e6611a22e493d276f24fa5a13328f\",\n" +
                "    \"node_id\": \"MDY6Q29tbWl0MTYxMjc3NzU2OjY3MTNjZTljMTEzZTY2MTFhMjJlNDkzZDI3NmYyNGZhNWExMzMyOGY=\",\n" +
                "    \"parents\":\n" +
                "    []\n" +
                "}";
        InputStream stream = new ByteArrayInputStream(mockCommitStatsData.getBytes(StandardCharsets.UTF_8));
        //System.out.println(stream);
        return stream;
    }

}
