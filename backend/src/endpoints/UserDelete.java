package endpoints;

import com.google.gson.*;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import database.DataSourceFactory;
import server.HTTPMessage;
import server.SSocket;
import server.Socketeer;
import java.sql.*;
import java.util.ArrayList;
import javax.sql.DataSource;
import management.Tracker;

/*Dirty delete, doesn't resolve groups or events or anything else, just deletes*/
public class UserDelete implements IAPIRoute {
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        int id = getCookie(request.getBody());
        if (id == -1) {
            //error
            String response = "{\"error\":\"Couldn't get id\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        try {
            /*
            Friends, groupuserjunction, groups, cb_groupusereventjunction, and users
            */
            MysqlConnectionPoolDataSource ds = null;  //mysql schedule database
            ds = DataSourceFactory.getDataSource();
            if (ds == null) {
                String response = "{\"error\":\"Couldn't get data source\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            Connection connection = null;  //used to connect to database
            Statement statement = null;  //statement to enter command
            ResultSet result = null;  //output after query

            connection = ds.getConnection();
            //create statement
            //statement = connection.createStatement();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            try {
                //delete friends
                //userID_1 and userID_2
                String query = "DELETE from friends WHERE userID_1=" + id + " OR userID_2=" + id + ";";
                System.out.println(query);
                statement.execute(query);
            }
            catch (Exception x) {
                x.printStackTrace();
            }
            try {
                //delete groupuser
                String query = "DELETE from group_user_junction WHERE userID=" + id + ";";
                System.out.println(query);
                statement.execute(query);
            }
            catch (Exception x) {
                x.printStackTrace();
            }
            try {
                //delete groups
                String query = "DELETE from groups WHERE creatorid=" + id + ";";
                System.out.println(query);
                statement.execute(query);
            }
            catch (Exception x) {
                x.printStackTrace();
            }
            try {
                //delete cb
                String query = "DELETE from group_user_junction WHERE userID=" + id + ";";
                System.out.println(query);
                statement.execute(query);
            }
            catch (Exception x) {
                x.printStackTrace();
            }
            try {
                //delete users
                String query = "DELETE from users WHERE id=" + id + ";";
                System.out.println(query);
                statement.execute(query);
            }
            catch (Exception x) {
                x.printStackTrace();
            }
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
            return;
        }
        catch (Exception e) {
            System.out.println("big error, this one");
            e.printStackTrace();
            String response = "{\"error\":\"big error\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
    }
    int getCookie(String body) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (!jobj.has("userid")) {
                return -1;
            }
            return jobj.get("userid").getAsInt();
        }
        catch (Exception e) {
            return -1;
        }
    }
}
