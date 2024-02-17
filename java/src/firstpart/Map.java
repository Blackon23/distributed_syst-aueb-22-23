package firstpart;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Map {

    private List<String> usernames;
    private List<Integer> results_no;

    public Map() {
        usernames   = new ArrayList<>();
        results_no  = new ArrayList<>();
    }
    
    // Μειώνει τα αποτελέσματα ανάλογα με τον χρήστη
    public void reduce_user_results(String uname) {
        
        for (int i = 0; i < usernames.size(); i++) {
            String get = usernames.get(i);
            
            if( get.equals(uname) ) {
                
                results_no.set(i, results_no.get(i)-1);
                break;
            }
        }
    }

    void put(String uname, int parts) {
        
        usernames.add(uname);
        results_no.add(parts);
    }

    boolean isReady(String uname) {
        
        for (int i = 0; i < usernames.size(); i++) {
            String get = usernames.get(i);
            
            // Αν έχουν επιστρέψει όλα τα results αυτού του χρήστη
            if( get.equals(uname) && results_no.get(i) == 0 ) {
                
                return true;
            }
        }
        
        return false;
    }
}