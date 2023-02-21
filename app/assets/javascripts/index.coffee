$ ->
  # Requests a web socket from the server for two-way fully duplex communication
  ws = new WebSocket $("#gitterific-home").data("ws-url")
  # On receiving a message, checks the response type and renders data accordingly
  console.log("here i am at socket creation")
  console.log(ws)
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.responseType
      when "repositoryDetails"
        $("#search-page").hide()
        $("#search-page-result").hide()
        $("#topic-page-result").hide()
        $("#user-details").hide()
        ComposeRepositoryDetailsHtml(message)
        $("#repository-details").show()
      when "searchResults"
        $("#search-page").show()
        ComposeSearchPageHtml(message)
        $("#search-page-result").show()
        $("#repository-details").hide()
        $("#topic-page-result").hide()
        $("#user-details").hide()
      when "topicsDetails"
        $("#search-page").hide()
        $("#search-page-result").hide()
        $("#repository-details").hide()
        $("#user-details").hide()
        ComposeTopicSearchHtml(message)
        $("#topic-page-result").show()
      when "userDetails"
        $("#search-page").hide()
        $("#search-page-result").hide()
        $("#repository-details").hide()
        $("#topic-page-result").hide()
        ComposeUserDetailsHtml(message)
        $("#user-details").show()
      when "issueStatisticsPage"
        $("#repository-details").hide()
        ComposeIssueStatisticsPageHtml(message)
        $("#issue-statistics").show()
      when "commitStatisticsPage"
        $("#repository-details").hide()
        ComposeCommitStatisticsPageHtml(message)
        $('#commit-statistics').show()

  $("#searchForm").submit (event) ->
      event.preventDefault()
      phrase = $("#phrase").val()
      if phrase == ""
        alert "search cant be empty"
        return false
      else
        ws.send(JSON.stringify({searchPage: phrase}))
        $("#phrase").val("")
        return

  $("#search-page-result").on "click", "a.repository-details", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({repositoryDetails: $(this).text(), username: $(this).attr("username")}))
    return
  $("#search-page-result").on "click", "a.topic-link", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({topicsDetails: $(this).text()}))
    return
  $("#topic-page-result").on "click", "a.topic-link", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({topicsDetails: $(this).text()}))
    return
  $("#topic-page-result").on "click", "a.repository-details", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({repositoryDetails: $(this).text(), username: $(this).attr("username")}))
    return
  $("#search-page-result").on "click", "a.user-details", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({userDetails: $(this).text(), username: $(this).attr("username")}))
    return
  $("#repository-details").on "click", "a.user-details", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({userDetails: $(this).text(), username: $(this).attr("username")}))
    return
  $("#reponavbar").on "click", "a.issue-stat-link", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({issueStatisticsPage: "",repositoryName: $(this).attr("repositoryName"), userName: $(this).attr("username")}))
    return
  $("#topic-page-result").on "click", "a.user-details", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({userDetails: $(this).text(), username: $(this).attr("username")}))
    return
  $("#reponavbar").on "click", "a.commit-statistics-link", (event) ->
    event.preventDefault()
    ws.send(JSON.stringify({commitStatisticsPage: "", repositoryName: $(this).attr("repositoryName"), userName: $(this).attr("username")}))
    return

ComposeSearchPageHtml =  (message) ->
  $("#search-page-result").empty()
  keys = message.searchMap.keys
  for key,value of message.searchMap
    if(typeof value == "object")
        $('#search-page-result').append "<br/><br/><b>" + "Search term :  " +key + "<br/><br/>"
        searchTable = $("<table>").prop("class", "table").prop("border","1")
        searchTable.append "<thead><tr><th>User</th><th>Repository</th><th>Topics</th></thead><tbody>"
        getSearchDetails value, searchTable
        $("#search-page-result").append(searchTable)


getSearchDetails = (objectValue, searchTable ) ->
        for key,value of objectValue
            searchData = $("<tr>")
            if(typeof value == "object")
                getSearchRepoValues value , searchData
            searchTable.append(searchData)


getSearchRepoValues = (objectValue, searchData ) ->
        for key,value of objectValue
            if(key=="owner")
                userLink = $("<a>").text(value).attr("class", "user-details").attr("username",value)
                owner = $("<td>").append(userLink).append("</td>")
            else if(key=="name")
                repositoryLink =  $("<a>").text(value).attr("class", "repository-details").attr("username",objectValue['owner'])
                repository = $("<td>").append(repositoryLink).append("</td>")
            else if(key=="topics")
                topicsData =  $("<td>")
                for element,val of value
                    topicLink =  $("<a>").text(val).attr("class","topic-link")
                    topicsData.append(topicLink).append("</td>")
        searchData.append(owner).append(repository).append(topicsData).append("</tr>")

ComposeTopicSearchHtml = (message) ->
    $("#mainBanner").empty()
    $("#mainBanner").removeAttr("style")
    $("#mainBanner").append($("<h1>").text("Topics Search"))
    $("#mainBanner").attr("style","margin-left: 450px;")
    $("#topic-page-result").empty()
    topicName = message.searchProfile.keyword
    $("#mainBanner").append($("<h3>").text("Repository from topic"+ topicName))
    searchTable = $("<table>").prop("class", "table").prop("border","1")
    searchTable.append "<thead><tr><th>Repository</th><th>User</th><th>Topics</th></thead><tbody>"
    for repository in message.searchProfile.repos
        searchData = $("<tr>")
        repositoryLink = $("<a>").text(repository.name).attr("class", "repository-details").attr("username",repository.owner)
        repository_user = $("<td>").append(repositoryLink).append("</td>")
        searchData.append(repository_user)
        userProfileLink = $("<a>").text(repository.owner).attr("class", "user-details").attr("username",repository.owner)
        userData  = $("<td>").append(userProfileLink).append("</td>")
        searchData.append(userProfileLink)
        topicList =$("<p>").text("")
        for topic in repository.topics
            topicLink = $("<a>").text(topic).attr("href", "/getReposByTopics/"+topic).attr("class","topic-link")
            topicList.append(topicLink)
        topicData = $("<td>").append(topicList).append("</td>")
        searchData.append(topicData)
        searchData.append($("</tr>"))
        searchTable.append(searchData)
    searchTable.append($("</tbody>"))
    $("#topic-page-result").append(searchTable)

ComposeRepositoryDetailsHtml = (message) ->
  $("#mainBanner").empty()
  $("#mainBanner").removeAttr("style")
  $("#mainBanner").append("<h1>").text("Repository Details")
  $("#mainBanner").attr("style","margin-left: 450px;")
  $("#repository-details").empty()


  repositoryName = message.repositoryDetails.name
  username = message.repositoryDetails.owner.login

  $("#reponavbar").empty()
  div = $("<div>").addClass("container-fluid")
  ul = $("<ul>").addClass("nav navbar-nav navbar-right").attr("id","repo-page-hyperlinks")
  issuesSpan = $('<span>').addClass("glyphicon glyphicon-stats").append("</span>")
  issuesSpan2 = $('<span>').addClass("glyphicon glyphicon-stats").append("</span>")
  issuesStats = $("<a>").attr("username",username).attr("repositoryName",repositoryName).attr("class","issue-stat-link").append("</a>").text(" Issues Statistics ").append(issuesSpan)
  li =  $("<li>").append(issuesStats).append("</li>")
  commitStats = $("<a>").attr("username",username).attr("repositoryName",repositoryName).attr("class","commit-statistics-link").append("</a>").text(" Commit Statistics ").append(issuesSpan2)
  li2 =  $("<li>").append(commitStats).append("</li>")
  ul.append(li).append(li2).append("</ul>")
  div.append(ul).append("</div>")
  $("#reponavbar").append(div)

  dlList = $("<dl>").prop("class","row")
  $('#repository-details').append(dlList)
  for key,value of message.repositoryDetails
    if(typeof value == "object")
      getRepositoryDetails value, repositoryName, dlList
    else if(key == "openIssues" || key == "watchers" || key == "description" || key == "name" || key == "hasWiki" || key == "hasIssues" || key == "hasDownloads" || key == "masterBranch" || key == "forks" || key == "size" )
      if(value!=null)
          h4key = $("<h4>").text(key).append("</h4>")
          dt = $("<dt>").prop("class", "col-sm-3").append(h4key)
          h4value = $("<h4>").text(value).append("</h4>")
          dd = $("<dd>").prop("class", "col-sm-9").append(h4value)
          dlList.append(dt).append(dd)
          $('#repository-details').append(dlList)
    else if(key == "createdAt" || key == "updatedAt" || key == "pushedAt")
      if(value!=null)
          h4key = $("<h4>").text(key).append("</h4>")
          dt = $("<dt>").prop("class", "col-sm-3").append(h4key)
          dateVal = new Date(value)
          h4value = $("<h4>").text(dateVal.toUTCString()).append("</h4>")
          dd = $("<dd>").prop("class", "col-sm-9").append(h4value)
          dlList.append(dt).append(dd)
          $('#repository-details').append(dlList)

  $('#repository-details').append "<br><b><h3>Issues of repository :  "+repositoryName+"</h3></b>"
  if message.issueListNode.length > 0
      issuesTable = $("<table>").prop("class", "table").prop("border","1")
      issuesTable.append "<thead><tr><th>Sl no.</th><th>HTML URL</th><th>State</th><th>Issue</th></thead><tbody>"
      $("#repository-details").append(issuesTable)
      for key,value of message.issueListNode
            if(typeof value == "object")
                issueData = $('<tr>')
                index = parseInt(key) + 1
                getIssuesListDetails value, issuesTable, issueData, index
            issuesTable.append(issueData)
            issuesTable.append("</tbody>")
      $("#repository-details").append(issuesTable).append("</tbody><br>")
  else
      $("#repository-details").append("No issues found")

getIssuesListDetails = (objectValue, issuesTable, issueData, index) ->
        for key,value of objectValue
            if(key=="title")
                issueTitle = $("<td>").text(value).append("</td>")
                issueData.append(issueTitle)
            if(key=="htmlUrl")
                issueSlNo = $("<td>").text(index).append("</td>")
                issueLink = $("<a>").text(value).attr("href",value).append("</a>")
                issueHtmlUrl = $("<td>").append(issueLink).append("</td>")
                issueData.append(issueSlNo).append(issueHtmlUrl)
            if(key=="state")
                issueState = $("<td>").text(value).append("</td>")
                issueData.append(issueState)

getRepositoryDetails = (objectValue, repositoryName, dlList ) ->
        for key,value of objectValue
            if(key == "login")
                  if(value!=null)
                      userProfileLink = $("<a>").text(value).attr("href", "/user-profile/" + repositoryName)
                      userProfileLink.addClass("user-details")
                      userProfileLink.attr("username",value)
                      h4key = $("<h4>").text(key).append("</h4>")
                      dt = $("<dt>").prop("class", "col-sm-3").append(h4key)
                      h4value = $("<h4>").append(userProfileLink).append("</h4>")
                      dd = $("<dd>").prop("class", "col-sm-9").append(h4value)
                      dlList.append(dt).append(dd)
                      $('#repository-details').append(dlList)
            else if(key == "htmlUrl")
                  if(value!=null)
                      userProfileLink = $("<a>").text(value+"/"+repositoryName).attr("href", value+"/"+ repositoryName)
                      userProfileLink.addClass("user-profile-link")
                      h4key = $("<h4>").text(key).append("</h4>")
                      dt = $("<dt>").prop("class", "col-sm-3").append(h4key)
                      h4value = $("<h4>").append(userProfileLink).append("</h4>")
                      dd = $("<dd>").prop("class", "col-sm-9").append(h4value)
                      dlList.append(dt).append(dd)
                      $('#repository-details').append(dlList)
            else if(key == "openIssues" || key == "updatedAt" || key == "pushedAt"  || key == "watchers" || key == "description" || key == "hasWiki" || key == "hasIssues" || key == "hasDownloads" || key == "masterBranch" || key == "forks" || key == "size" )
                   if(value!=null)
                       dt = $("<dt>").prop("class", "col-sm-3").text(key)
                       dd = $("<dd>").prop("class", "col-sm-9").text(value)
                       dlList.append(dt).append(dd)
                       $('#repository-details').append(dlList)

ComposeUserDetailsHtml = (message) ->
   $('#reponavbar').empty()
   $("#mainBanner").empty()
   $("#mainBanner").removeAttr("style")
   $("#mainBanner").append("<h1>").text("User Details")
   $("#mainBanner").attr("style","margin-left: 450px;")
   $("#user-details").empty()

   username = message.userDetails.login
   dlList = $("<dl>").prop("class","row")
   $('#user-details').append(dlList)
   for key,value of message.userDetails
     if(key == "login" || key == "publicRepos" || key == "htmlUrl")
       dt = $("<dt>").prop("class", "col-sm-3").text(key)
       dd = $("<dd>").prop("class", "col-sm-9").text(value)
       dlList.append(dt).append(dd)
       $('#user-details').append(dlList)
   $('#user-details').append "<br><b><h3>Repositories of User :  "+username+"</h3></b>"
   if message.repositoryList.length > 0
    repositoriesTable = $("<table>").prop("class", "table").prop("border","1")
    repositoriesTable.append "<thead><tr><th>Sl no.</th><th>Issues</th></thead><tbody>"
    $("#user-details").append(repositoriesTable)
    for key,value of message.repositoryList
        repositoryData = $('<tr>')
        index = parseInt(key) + 1
        repositorySlNo = $("<td>").text(index).append("</td>")
        repositoryTitle = $("<td>").text(value).append("</td>")
        repositoryData.append(repositorySlNo).append(repositoryTitle)
        repositoriesTable.append(repositoryData)
        repositoriesTable.append("</tbody>")
    $("#user-details").append(repositoriesTable).append("</tbody><br>")

ComposeIssueStatisticsPageHtml = (message) ->
  $('#reponavbar').empty()
  $("#mainBanner").empty()
  $("#mainBanner").removeAttr("style")
  $("#mainBanner").append("<h2>").text("Issue Word Level Statistics")
  $("#mainBanner").attr("style","margin-left: 400px;")
  $("#issue-statistics").empty()

  repositoryName = message.respositoryName

  $('#issue-statistics').append("<b>").attr( "style","padding: 5px;").append("Issue Word Level Statistics for " + repositoryName)

  if message.issueStatList.wordfrequency != undefined
        issueStatTable = $("<table>").prop("class", "table").prop("border","1")
        issueStatTable.append "<thead><tr><th>Issue Keyword</th><th>Count</th></thead><tbody>"
        $("#issue-statistics").append(issueStatTable)

        for key,value of message.issueStatList.wordfrequency

              issuesWordCountData = $('<tr>')
              issuesWord = $("<td>").text(key).append("</td>")
              issuesWordCount = $("<td>").text(value).append("</td>")
              issuesWordCountData.append(issuesWord).append(issuesWordCount)
              issueStatTable.append(issuesWordCountData)
              issueStatTable.append("</tbody>")
        $("#issue-statistics").append(issueStatTable).append("</tbody><br>")
    else
        $("#issue-statistics").append("No issues found")

ComposeCommitStatisticsPageHtml = (message) ->
  $('#reponavbar').empty()
  $("#mainBanner").empty()
  $("#mainBanner").removeAttr("style")
  $("#mainBanner").append("<h2>").text("Commit Statistics")
  $("#mainBanner").attr("style","margin-left: 400px;")
  $("#commit-statistics").empty()

  repositoryName = message.repositoryName

  $('#commit-statistics').append("<b>").attr( "style","padding: 5px;").append("Commit Statistics for " + repositoryName)

  dlList = $("<dl>").prop("class","row")
  $('#commit-statistics').append(dlList)
  totalCommitsOnRepositoryKey = $("<h4>").text("Total commits on Repository: ").append("</h4>")
  dt = $("<dt>").prop("class", "col-sm-3").append(totalCommitsOnRepositoryKey)
  totalCommitsOnRepositoryValue = $("<h4>").text(message.commitStatsList.totalCommitsOnRepository).append("</h4>")
  dd = $("<dd>").prop("class", "col-sm-9").append(totalCommitsOnRepositoryValue)
  dlList.append(dt).append(dd)
  $('#commit-statistics').append(dlList)

  dlList = $("<dl>").prop("class","row")
  $('#commit-statistics').append(dlList)
  minimumAdditionsOnRepositoryKey = $("<h4>").text("Minimum Additions: ").append("</h4>")
  dt = $("<dt>").prop("class", "col-sm-3").append(minimumAdditionsOnRepositoryKey)
  minimumAdditionsOnRepositoryValue = $("<h4>").text(message.commitStatsList.minimumAdditions).append("</h4>")
  dd = $("<dd>").prop("class", "col-sm-9").append(minimumAdditionsOnRepositoryValue)
  dlList.append(dt).append(dd)
  $('#commit-statistics').append(dlList)

  dlList = $("<dl>").prop("class","row")
  $('#commit-statistics').append(dlList)
  maximumAdditionsOnRepositoryKey = $("<h4>").text("Maximum Additions: ").append("</h4>")
  dt = $("<dt>").prop("class", "col-sm-3").append(maximumAdditionsOnRepositoryKey)
  maximumAdditionsOnRepositoryValue = $("<h4>").text(message.commitStatsList.maximumAdditions).append("</h4>")
  dd = $("<dd>").prop("class", "col-sm-9").append(maximumAdditionsOnRepositoryValue)
  dlList.append(dt).append(dd)
  $('#commit-statistics').append(dlList)

  dlList = $("<dl>").prop("class","row")
  $('#commit-statistics').append(dlList)
  minimumDeletionsOnRepositoryKey = $("<h4>").text("Minimum Deletions: ").append("</h4>")
  dt = $("<dt>").prop("class", "col-sm-3").append(minimumDeletionsOnRepositoryKey)
  minimumDeletionsOnRepositoryValue = $("<h4>").text(message.commitStatsList.minimumDeletions).append("</h4>")
  dd = $("<dd>").prop("class", "col-sm-9").append(minimumDeletionsOnRepositoryValue)
  dlList.append(dt).append(dd)
  $('#commit-statistics').append(dlList)

  dlList = $("<dl>").prop("class","row")
  $('#commit-statistics').append(dlList)
  maximumDeletionsOnRepositoryKey = $("<h4>").text("Maximum Deletions: ").append("</h4>")
  dt = $("<dt>").prop("class", "col-sm-3").append(maximumDeletionsOnRepositoryKey)
  maximumDeletionsOnRepositoryValue = $("<h4>").text(message.commitStatsList.maximumDeletions).append("</h4>")
  dd = $("<dd>").prop("class", "col-sm-9").append(maximumDeletionsOnRepositoryValue)
  dlList.append(dt).append(dd)
  $('#commit-statistics').append(dlList)

  dlList = $("<dl>").prop("class","row")
  $('#commit-statistics').append(dlList)
  averageAdditionsOnRepositoryKey = $("<h4>").text("Average Additions: ").append("</h4>")
  dt = $("<dt>").prop("class", "col-sm-3").append(averageAdditionsOnRepositoryKey)
  averageAdditionsOnRepositoryValue = $("<h4>").text(message.commitStatsList.averageAdditions).append("</h4>")
  dd = $("<dd>").prop("class", "col-sm-9").append(averageAdditionsOnRepositoryValue)
  dlList.append(dt).append(dd)
  $('#commit-statistics').append(dlList)

  dlList = $("<dl>").prop("class","row")
  $('#commit-statistics').append(dlList)
  averageDeletionsOnRepositoryKey = $("<h4>").text("Average Deletions: ").append("</h4>")
  dt = $("<dt>").prop("class", "col-sm-3").append(averageDeletionsOnRepositoryKey)
  averageDeletionsOnRepositoryValue = $("<h4>").text(message.commitStatsList.averageDeletions).append("</h4>")
  dd = $("<dd>").prop("class", "col-sm-9").append(averageDeletionsOnRepositoryValue)
  dlList.append(dt).append(dd)
  $('#commit-statistics').append(dlList)

  if message.commitStatsList.mapOfUserAndCommits != undefined
    commitStatsTable = $("<table>").prop("class", "table").prop("border","1")
    commitStatsTable.append "<thead><tr><th>User Name</th><th>Count</th></thead><tbody>"
    $("#commit-statistics").append(commitStatsTable)
    for key,value of message.commitStatsList.mapOfUserAndCommits
        commitCountData = $('<tr>')
        userNameData = $("<td>").text(key).append("</td>")
        userCommitCount = $("<td>").text(value).append("</td>")
        commitCountData.append(userNameData).append(userCommitCount)
        commitStatsTable.append(commitCountData)
        commitStatsTable.append("</tbody>")
        $("#commit-statistics").append(commitStatsTable).append("</tbody><br>")
  else
    $("#commit-statistics").append("No User-Commits Data found")
