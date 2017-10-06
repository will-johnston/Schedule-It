import database.AddOrRemoveFriendsInDb;
import org.junit.Assert;
import org.junit.Test;

public class DatabaseTests {

    //in progress
    @Test
    public void AddOrRemoveFriendsOutputs() {
        String user1 = "will";
        String user2 = "another";
        String user3 = "check";
        String user4 = "FLEM";
        String user5 = "user211";
        String notuser1 = "not a user";
        String notuser2 = "NOTAUSER";

        boolean add = true;
        boolean remove = false;

        //assert statements
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user1, user2, add));
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user1, user3, add));
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user1, user4, add));
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user1, user5, add));
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user2, user5, add));
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user2, user3, add));
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user3, user2, add));  //duplicate add friend
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user1, user2, add));  //another duplicate
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user1, user2, remove));
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user3, user2, remove));
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user1, user4, remove));
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user2, user5, remove));
        Assert.assertTrue(AddOrRemoveFriendsInDb.addOrRemoveFriend(user1, user2, remove));  //duplicate remove

        //friends list should now be:
        /*
        1-3
        1-5
         */

        Assert.assertFalse(AddOrRemoveFriendsInDb.addOrRemoveFriend(notuser1, user1, add));
        Assert.assertFalse(AddOrRemoveFriendsInDb.addOrRemoveFriend(notuser1, null, add));
        Assert.assertFalse(AddOrRemoveFriendsInDb.addOrRemoveFriend(null, null, add));
        Assert.assertFalse(AddOrRemoveFriendsInDb.addOrRemoveFriend(notuser1, notuser2, add));
        Assert.assertFalse(AddOrRemoveFriendsInDb.addOrRemoveFriend(notuser2, user3, add));
        Assert.assertFalse(AddOrRemoveFriendsInDb.addOrRemoveFriend(user4, notuser2, add));
        Assert.assertFalse(AddOrRemoveFriendsInDb.addOrRemoveFriend(null, notuser2, add));
        Assert.assertFalse(AddOrRemoveFriendsInDb.addOrRemoveFriend(user1, user1, add));  //duplicate user
    }
}