import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by anubhabmajumdar on 4/17/17.
 */
public class BTReturnObject {
    ArrayList<PVector> path;
    String action;

    public BTReturnObject(ArrayList<PVector> path, String action) {
        this.path = path;
        this.action = action;
    }

    public ArrayList<PVector> getPath() {
        return path;
    }

    public String getAction() {
        return action;
    }
}
