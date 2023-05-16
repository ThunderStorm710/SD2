package com.example.servingwebcontent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// TODO: Class Annotations
public class MyHackerNewsController {
    private static final Logger logger = LoggerFactory.getLogger(MyHackerNewsController.class);

    // TODO: Method Annotations
    public List<HackerNewsItemRecord> hackerNewsTopStories(String pesq) {
        // TODO: Get IDs of top stories

        // TODO: Iterate through each ID and check if it contains the "search" terms

        try {
            // Initiate the REST client.
            URL url = new URL("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // HTTP Verb
            connection.setRequestMethod("GET");
            // Get requests data from the server.

            // We are interested in the output
            connection.setDoOutput(true);

            // If there is a 3xx error, we want to know.
            connection.setInstanceFollowRedirects(false);

            // The Accept header defines what kind of formats we are interested in.
            connection.setRequestProperty("Accept", "application/json");
            // You should play with "*/*", "application/xml" and "application/json"
            // JSON might need a third party library to parse the response.

            // User Agent is the name of your application.
            connection.setRequestProperty("User-agent", "Pablo v1");
            // Some of the most common are Mozilla, Internet Explorer and GoogleBot.


            // If we get a Redirect or an Error (3xx, 4xx and 5xx)
            if (connection.getResponseCode() >= 300) {
                // We want more information about what went wrong.z
            }

            // Response body from InputStream.
            //InputSource inputSource = new InputSource(connection.getInputStream());

            // XPath is a way of reading XML files.
            ObjectMapper objectMapper = new ObjectMapper();
            //XPathFactory factory=XPathFactory.newInstance();
            //XPath xPath=factory.newXPath();

            // here we are querying the document (much like SQL) for all the item tags inside row elements.
            //NodeList nodes = (NodeList) xPath.evaluate("/board/row/item", inputSource, XPathConstants.NODESET);
            // The last argument defines the type of result we are looking for. Might be NODESEQ for a list of Nodes
            // or NODE for a single node.
            JsonNode jsonNode = objectMapper.readTree(connection.getInputStream());

            //System.out.println(jsonNode);

            // We don't need the connection anymore once we get the nodes.
            connection.disconnect();

            //criar lista
            ArrayList<HackerNewsItemRecord> TopStories = new ArrayList<>();

            // Pretty printing of output
            System.out.println("===========================================");
            for (JsonNode node : jsonNode) {
                String id = node.asText();
                //System.out.println(id);
                if (FindStory(id, pesq) != null) {
                    HackerNewsItemRecord story = FindStory(id, pesq);
                    TopStories.add(story);
                }
            }

            System.out.println("===========================================");
            for (HackerNewsItemRecord item : TopStories) {
                System.out.println(item);
            }
            return TopStories;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private HackerNewsItemRecord FindStory(String id, String pesq) {

        try {
            // Initiate the REST client.
            URL url = new URL("https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // HTTP Verb
            connection.setRequestMethod("GET");
            // Get requests data from the server.

            // We are interested in the output
            connection.setDoOutput(true);

            // If there is a 3xx error, we want to know.
            connection.setInstanceFollowRedirects(false);

            // The Accept header defines what kind of formats we are interested in.
            connection.setRequestProperty("Accept", "application/json");
            // You should play with "*/*", "application/xml" and "application/json"
            // JSON might need a third party library to parse the response.

            // User Agent is the name of your application.
            //connection.setRequestProperty("User-agent", "Pablo v1");
            // Some of the most common are Mozilla, Internet Explorer and GoogleBot.


            // If we get a Redirect or an Error (3xx, 4xx and 5xx)
            if (connection.getResponseCode() >= 300) {
                // We want more information about what went wrong.z
            }

            // Response body from InputStream.
            //InputSource inputSource = new InputSource(connection.getInputStream());

            // XPath is a way of reading XML files.
            ObjectMapper objectMapper = new ObjectMapper();
            //XPathFactory factory=XPathFactory.newInstance();
            //XPath xPath=factory.newXPath();

            // here we are querying the document (much like SQL) for all the item tags inside row elements.
            //NodeList nodes = (NodeList) xPath.evaluate("/board/row/item", inputSource, XPathConstants.NODESET);
            // The last argument defines the type of result we are looking for. Might be NODESEQ for a list of Nodes
            // or NODE for a single node.
            JsonNode jsonNode = objectMapper.readTree(connection.getInputStream());

            //System.out.println(jsonNode);

            // We don't need the connection anymore once we get the nodes.
            connection.disconnect();

            // Pretty printing of output


            //for (JsonNode node : jsonNode) {
            //System.out.println(node.get("title").asText());
            //System.out.println(node);
            /*
            if (jsonNode.get("title") != null){
                String title = jsonNode.get("title").asText();
                //System.out.println("++++++++++");
                //System.out.println(title);
                //System.out.println(pesq);
                //System.out.println("...............");

                if (title.contains(pesq)) {
                    System.out.println("A substring '" + pesq + "' está presente na string.");

                    Integer id_aux = jsonNode.get("id").asInt();
                    Boolean deleted = jsonNode.get("deleted") != null ? jsonNode.get("deleted").asBoolean() : null;
                    String type = jsonNode.get("type") != null ? jsonNode.get("type").asText() : null;
                    String by = jsonNode.get("by") != null ? jsonNode.get("by").asText() : null;
                    Long time = jsonNode.get("time") != null ? jsonNode.get("time").asLong() : null;
                    String text_aux = jsonNode.get("text") != null ? jsonNode.get("text").asText() : null;
                    Boolean dead = jsonNode.get("dead") != null ? jsonNode.get("dead").asBoolean() : null;
                    String parent = jsonNode.get("parent") != null ? jsonNode.get("parent").asText() : null;
                    Integer poll = jsonNode.get("poll") != null ? jsonNode.get("poll").asInt() : null;
                    List<Integer> kids = jsonNode.get("kids") != null ? Arrays.asList(jsonNode.get("kids").asInt()) : null;
                    String url_aux = jsonNode.get("url") != null ? jsonNode.get("url").asText() : null;
                    Integer score = jsonNode.get("score").isNull() ? null : jsonNode.get("score").asInt();
                    String title_aux = jsonNode.get("title") != null ? jsonNode.get("title").asText() : null;
                    List<Integer> parts = jsonNode.get("parts") != null ? Arrays.asList(jsonNode.get("parts").asInt()) : null;
                    Integer descendants = jsonNode.get("descendants").isNull() ? null : jsonNode.get("descendants").asInt();

                    HackerNewsItemRecord story = new HackerNewsItemRecord(id_aux, deleted, type, by, time, text_aux, dead, parent, poll, kids, url_aux, score, title, parts, descendants);
                    //HackerNewsItemRecord story = new HackerNewsItemRecord(jsonNode.get("id").asInt(), jsonNode.get("deleted").asBoolean(), jsonNode.get("type").asText(), jsonNode.get("by").asText(), jsonNode.get("time").asLong(), jsonNode.get("text").asText(), jsonNode.get("dead").asBoolean(), jsonNode.get("parent").asText(), jsonNode.get("poll").asInt(),  (List) jsonNode.get("kids"), jsonNode.get("url").asText(), jsonNode.get("score").asInt(), jsonNode.get("title").asText(), (List) jsonNode.get("parts"), jsonNode.get("descendants").asInt());
                    return story;
                } else {
                    System.out.println("A substring '" + pesq + "' não está presente na string.");
                }
            }else

            if(jsonNode.get("text") != null){
                String text = jsonNode.get("text").asText();
                if (text.contains(pesq)) {
                    System.out.println("A substring '" + pesq + "' está presente na string.");
                    Integer id_aux = jsonNode.get("id").asInt();
                    Boolean deleted = jsonNode.get("deleted") != null ? jsonNode.get("deleted").asBoolean() : null;
                    String type = jsonNode.get("type") != null ? jsonNode.get("type").asText() : null;
                    String by = jsonNode.get("by") != null ? jsonNode.get("by").asText() : null;
                    Long time = jsonNode.get("time") != null ? jsonNode.get("time").asLong() : null;
                    String text_aux = jsonNode.get("text") != null ? jsonNode.get("text").asText() : null;
                    Boolean dead = jsonNode.get("dead") != null ? jsonNode.get("dead").asBoolean() : null;
                    String parent = jsonNode.get("parent") != null ? jsonNode.get("parent").asText() : null;
                    Integer poll = jsonNode.get("poll") != null ? jsonNode.get("poll").asInt() : null;
                    List<Integer> kids = jsonNode.get("kids") != null ? Arrays.asList(jsonNode.get("kids").asInt()) : null;
                    String url_aux = jsonNode.get("url") != null ? jsonNode.get("url").asText() : null;
                    Integer score = jsonNode.get("score").isNull() ? null : jsonNode.get("score").asInt();
                    String title_aux = jsonNode.get("title") != null ? jsonNode.get("title").asText() : null;
                    List<Integer> parts = jsonNode.get("parts") != null ? Arrays.asList(jsonNode.get("parts").asInt()) : null;
                    Integer descendants = jsonNode.get("descendants").isNull() ? null : jsonNode.get("descendants").asInt();
                    //HackerNewsItemRecord story = new HackerNewsItemRecord(jsonNode.get("id").asInt(), jsonNode.get("deleted").asBoolean(), jsonNode.get("type").asText(), jsonNode.get("by").asText(), jsonNode.get("time").asLong(), jsonNode.get("text").asText(), jsonNode.get("dead").asBoolean(), jsonNode.get("parent").asText(), jsonNode.get("poll").asInt(),  (List) jsonNode.get("kids"), jsonNode.get("url").asText(), jsonNode.get("score").asInt(), jsonNode.get("title").asText(), (List) jsonNode.get("parts"), jsonNode.get("descendants").asInt());
                    HackerNewsItemRecord story = new HackerNewsItemRecord(id_aux, deleted, type, by, time, text, dead, parent, poll, kids, url_aux, score, title_aux, parts, descendants);
                    return story;
                } else {
                    System.out.println("A substring '" + pesq + "' não está presente na string.");
                }
            }*/

            if (jsonNode.get("text") != null) {
                String text = jsonNode.get("text").asText();

                String[] palavras = text.split(" ");

                boolean palavraEncontrada = false;
                for (String p : palavras) {
                    if (p.equalsIgnoreCase(pesq)) {
                        palavraEncontrada = true;
                        break;
                    }
                }

                if (palavraEncontrada) {
                    /*
                    //System.out.println("A substring '" + pesq + "' está presente na string.");
                    Integer id_aux = jsonNode.get("id").asInt();
                    Boolean deleted = jsonNode.get("deleted") != null ? jsonNode.get("deleted").asBoolean() : null;
                    String type = jsonNode.get("type") != null ? jsonNode.get("type").asText() : null;
                    String by = jsonNode.get("by") != null ? jsonNode.get("by").asText() : null;
                    Long time = jsonNode.get("time") != null ? jsonNode.get("time").asLong() : null;
                    //String text_aux = jsonNode.get("text") != null ? jsonNode.get("text").asText() : null;
                    Boolean dead = jsonNode.get("dead") != null ? jsonNode.get("dead").asBoolean() : null;
                    String parent = jsonNode.get("parent") != null ? jsonNode.get("parent").asText() : null;
                    Integer poll = jsonNode.get("poll") != null ? jsonNode.get("poll").asInt() : null;
                    List<Integer> kids = jsonNode.get("kids") != null ? Arrays.asList(jsonNode.get("kids").asInt()) : null;
                    String url_aux = jsonNode.get("url") != null ? jsonNode.get("url").asText() : null;
                    Integer score = jsonNode.get("score").isNull() ? null : jsonNode.get("score").asInt();
                    String title_aux = jsonNode.get("title") != null ? jsonNode.get("title").asText() : null;
                    List<Integer> parts = jsonNode.get("parts") != null ? Arrays.asList(jsonNode.get("parts").asInt()) : null;
                    Integer descendants = jsonNode.get("descendants").isNull() ? null : jsonNode.get("descendants").asInt();
                    */
                    Integer id_aux = jsonNode.has("id") && !jsonNode.get("id").isNull() ? jsonNode.get("id").asInt() : null;
                    Boolean deleted = jsonNode.has("deleted") && !jsonNode.get("deleted").isNull() ? jsonNode.get("deleted").asBoolean() : null;
                    String type = jsonNode.get("type") != null ? jsonNode.get("type").asText() : null;
                    String by = jsonNode.get("by") != null ? jsonNode.get("by").asText() : null;
                    Long time = jsonNode.has("time") && !jsonNode.get("time").isNull() ? jsonNode.get("time").asLong() : null;
                    Boolean dead = jsonNode.has("dead") && !jsonNode.get("dead").isNull() ? jsonNode.get("dead").asBoolean() : null;
                    String parent = jsonNode.get("parent") != null ? jsonNode.get("parent").asText() : null;
                    Integer poll = jsonNode.has("poll") && !jsonNode.get("poll").isNull() ? jsonNode.get("poll").asInt() : null;
                    List<Integer> kids = null;
                    JsonNode submittedNode = jsonNode.get("kids");
                    if (submittedNode != null && submittedNode.isArray()) {
                        kids = new ArrayList<>();
                        for (JsonNode node : submittedNode) {
                            if (node.isInt()) {
                                kids.add(node.asInt());
                            }
                        }
                    }
                    String url_aux = jsonNode.get("url") != null ? jsonNode.get("url").asText() : null;
                    Integer score = jsonNode.has("score") && !jsonNode.get("score").isNull() ? jsonNode.get("score").asInt() : null;
                    String title_aux = jsonNode.get("title") != null ? jsonNode.get("title").asText() : null;
                    List<Integer> parts = null;
                    JsonNode submittedNode1 = jsonNode.get("parts");
                    if (submittedNode1 != null && submittedNode1.isArray()) {
                        parts = new ArrayList<>();
                        for (JsonNode node : submittedNode1) {
                            if (node.isInt()) {
                                parts.add(node.asInt());
                            }
                        }
                    }
                    Integer descendants = jsonNode.has("descendants") && !jsonNode.get("descendants").isNull() ? jsonNode.get("descendants").asInt() : null;
                    HackerNewsItemRecord story = new HackerNewsItemRecord(id_aux, deleted, type, by, time, text, dead, parent, poll, kids, url_aux, score, title_aux, parts, descendants);

                    return story;
                }
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<HackerNewsItemRecord> UserTopStories(String user_id) {

        try {
            // Initiate the REST client.
            URL url = new URL("https://hacker-news.firebaseio.com/v0/user/" + user_id + ".json?print=pretty");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // HTTP Verb
            connection.setRequestMethod("GET");
            // Get requests data from the server.

            // We are interested in the output
            connection.setDoOutput(true);

            // If there is a 3xx error, we want to know.
            connection.setInstanceFollowRedirects(false);

            // The Accept header defines what kind of formats we are interested in.
            connection.setRequestProperty("Accept", "application/json");
            // You should play with "*/*", "application/xml" and "application/json"
            // JSON might need a third party library to parse the response.

            // User Agent is the name of your application.
            connection.setRequestProperty("User-agent", "Pablo v1");
            // Some of the most common are Mozilla, Internet Explorer and GoogleBot.


            // If we get a Redirect or an Error (3xx, 4xx and 5xx)
            if (connection.getResponseCode() >= 300) {
                // We want more information about what went wrong.z
            }

            // Response body from InputStream.
            //InputSource inputSource = new InputSource(connection.getInputStream());

            // XPath is a way of reading XML files.
            ObjectMapper objectMapper = new ObjectMapper();
            //XPathFactory factory=XPathFactory.newInstance();
            //XPath xPath=factory.newXPath();

            JsonNode jsonNode = objectMapper.readTree(connection.getInputStream());
            if (jsonNode == null){
                return null;
            }

            System.out.println(jsonNode);

            connection.disconnect();

            ArrayList<HackerNewsItemRecord> userTopStories = new ArrayList<>();

            // Pretty printing of output
            System.out.println("===========================================");

            if (jsonNode.get("id").asText().equals(user_id)) {
                //List<Integer> userStories = jsonNode.get("submitted") != null ? Arrays.asList(jsonNode.get("submitted").asInt()) : null;
                List<Integer> userStories = null;
                JsonNode submittedNode = jsonNode.get("submitted");
                if (submittedNode != null && submittedNode.isArray()) {
                    userStories = new ArrayList<>();
                    for (JsonNode node : submittedNode) {
                        if (node.isInt()) {
                            userStories.add(node.asInt());
                        }
                    }
                }
                System.out.println(userStories);
                if (userStories != null) {
                    for (Integer id : userStories) {
                        if (FindUserStory(id.toString()) != null) {
                            HackerNewsItemRecord story = FindUserStory(id.toString());
                            userTopStories.add(story);
                        }
                    }
                } else {
                    System.out.println("User nao tem nenhuma story associada");
                }
            }

            System.out.println("===========================================");
            for (HackerNewsItemRecord item : userTopStories) {
                System.out.println(item);
            }
            return userTopStories;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private HackerNewsItemRecord FindUserStory(String id) {

        try {
            // Initiate the REST client.
            URL url = new URL("https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // HTTP Verb
            connection.setRequestMethod("GET");
            // Get requests data from the server.

            // We are interested in the output
            connection.setDoOutput(true);

            // If there is a 3xx error, we want to know.
            connection.setInstanceFollowRedirects(false);

            // The Accept header defines what kind of formats we are interested in.
            connection.setRequestProperty("Accept", "application/json");
            // You should play with "*/*", "application/xml" and "application/json"
            // JSON might need a third party library to parse the response.

            // User Agent is the name of your application.
            //connection.setRequestProperty("User-agent", "Pablo v1");
            // Some of the most common are Mozilla, Internet Explorer and GoogleBot.


            // If we get a Redirect or an Error (3xx, 4xx and 5xx)
            if (connection.getResponseCode() >= 300) {
                // We want more information about what went wrong.z
            }

            // Response body from InputStream.
            //InputSource inputSource = new InputSource(connection.getInputStream());

            // XPath is a way of reading XML files.
            ObjectMapper objectMapper = new ObjectMapper();
            //XPathFactory factory=XPathFactory.newInstance();
            //XPath xPath=factory.newXPath();

            // here we are querying the document (much like SQL) for all the item tags inside row elements.
            //NodeList nodes = (NodeList) xPath.evaluate("/board/row/item", inputSource, XPathConstants.NODESET);
            // The last argument defines the type of result we are looking for. Might be NODESEQ for a list of Nodes
            // or NODE for a single node.
            JsonNode jsonNode = objectMapper.readTree(connection.getInputStream());


            // We don't need the connection anymore once we get the nodes.
            connection.disconnect();

            // Pretty printing of output

            if (jsonNode.get("type").asText().equals("story")) {
                //System.out.println(jsonNode);
                Integer id_aux = jsonNode.has("id") && !jsonNode.get("id").isNull() ? jsonNode.get("id").asInt() : null;
                Boolean deleted = jsonNode.has("deleted") && !jsonNode.get("deleted").isNull() ? jsonNode.get("deleted").asBoolean() : null;
                String type = jsonNode.get("type") != null ? jsonNode.get("type").asText() : null;
                String by = jsonNode.get("by") != null ? jsonNode.get("by").asText() : null;
                Long time = jsonNode.has("time") && !jsonNode.get("time").isNull() ? jsonNode.get("time").asLong() : null;
                String text = jsonNode.get("text") != null ? jsonNode.get("text").asText() : null;
                Boolean dead = jsonNode.has("dead") && !jsonNode.get("dead").isNull() ? jsonNode.get("dead").asBoolean() : null;
                String parent = jsonNode.get("parent") != null ? jsonNode.get("parent").asText() : null;
                Integer poll = jsonNode.has("poll") && !jsonNode.get("poll").isNull() ? jsonNode.get("poll").asInt() : null;
                List<Integer> kids = null;
                JsonNode submittedNode = jsonNode.get("kids");
                if (submittedNode != null && submittedNode.isArray()) {
                    kids = new ArrayList<>();
                    for (JsonNode node : submittedNode) {
                        if (node.isInt()) {
                            kids.add(node.asInt());
                        }
                    }
                }
                String url_aux = jsonNode.get("url") != null ? jsonNode.get("url").asText() : null;
                Integer score = jsonNode.has("score") && !jsonNode.get("score").isNull() ? jsonNode.get("score").asInt() : null;
                String title_aux = jsonNode.get("title") != null ? jsonNode.get("title").asText() : null;
                List<Integer> parts = null;
                JsonNode submittedNode1 = jsonNode.get("parts");
                if (submittedNode1 != null && submittedNode1.isArray()) {
                    parts = new ArrayList<>();
                    for (JsonNode node : submittedNode1) {
                        if (node.isInt()) {
                            parts.add(node.asInt());
                        }
                    }
                }
                Integer descendants = jsonNode.has("descendants") && !jsonNode.get("descendants").isNull() ? jsonNode.get("descendants").asInt() : null;

                HackerNewsItemRecord story = new HackerNewsItemRecord(id_aux, deleted, type, by, time, text, dead, parent, poll, kids, url_aux, score, title_aux, parts, descendants);

                return story;

            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
