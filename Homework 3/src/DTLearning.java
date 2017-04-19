import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.*;

/**
 * Created by anubhabmajumdar on 4/18/17.
 */
public class DTLearning {

    PrintWriter pw;

    public void getFeatures(String fileName)
    {
        try {
            pw = new PrintWriter(new File("features.csv"));
            StringBuilder sb = new StringBuilder();
            sb.append("Distance Between Objects");
            sb.append(',');
            sb.append("In Room");
            sb.append(',');
            sb.append("Action");
            sb.append('\n');
            pw.write(sb.toString());
        }
        catch (Exception ex)
        {
            System.out.println("Cannot open file features.csv");
        }

        /* Followed the example provided here - http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html */
        StringBuffer stringBuffer = readFile(fileName);
        String toString = stringBuffer.toString();
        /* -------------------------------------------------------------------------------------------------------------------- */
        /* Followed the example provided here - http://stackoverflow.com/questions/454908/split-java-string-by-new-line */
        String rawData[] = toString.split("\\r?\\n");

        for (int i=1; i<rawData.length; i++)
        {
            String cur = rawData[i];
/* Followed the example provided here - http://stackoverflow.com/questions/1635764/string-parsing-in-java-with-delimeter-tab-t-using-split */
            String vals[] = cur.split(",");

            float charPosX = Float.parseFloat(vals[0]);
            float charPosY = Float.parseFloat(vals[1]);

            float monsterPosX = Float.parseFloat(vals[9]);
            float monsterPosY = Float.parseFloat(vals[10]);

            String action = vals[vals.length-1];

            try
            {
                recordFeatures(charPosX, charPosY, monsterPosX, monsterPosY, action);
                System.out.println("Wrote in file features.csv");
            }
            catch (Exception ex)
            {
                System.out.println("Cannot write in file features.csv");
            }

        }
        pw.close();

    }

    public void makeDTFromFeatures(String fileName)
    {
        /* Followed the example provided here - http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html */
        StringBuffer stringBuffer = readFile(fileName);
        String toString = stringBuffer.toString();
        /* -------------------------------------------------------------------------------------------------------------------- */
        /* Followed the example provided here - http://stackoverflow.com/questions/454908/split-java-string-by-new-line */
        String rawData[] = toString.split("\\r?\\n");

        ArrayList<String[]> features = new ArrayList<String[]>();
        ArrayList<String[]> actions = new ArrayList<String[]>();
        for (int i=1;i<rawData.length;i++)
        {
            String cur = rawData[i];
/* Followed the example provided here - http://stackoverflow.com/questions/1635764/string-parsing-in-java-with-delimeter-tab-t-using-split */
            String vals[] = cur.split(",");
            features.add(Arrays.copyOfRange(vals, 0, vals.length-1));
            actions.add(Arrays.copyOfRange(vals, vals.length-1, vals.length));
        }
        conditionalEntropy(features, actions, 1);
        //learningDT(features, actions, 1);
//        System.out.println(((String[]) (features.get(0)))[1]);
//        System.out.println(((String[]) (actions.get(0)))[0]);
    }

    public void learningDT(ArrayList<String[]> features, ArrayList<String[]> actions, int height)
    {
        System.out.println(height);

        if (height>features.get(0).length)
        {
            return;
        }
        learningDT(features, actions, height+1);
        learningDT(features, actions, height+1);
        return;

    }

    public double conditionalEntropy(ArrayList<String[]> features, ArrayList<String[]> actions, int X)
    {
        HashMap<String, Integer> typesX = new HashMap<String, Integer>();
        int tempCount = 0;
        typesX.put(features.get(0)[X], tempCount++);

        for (int i=1;i<features.size();i++)
        {
            if (typesX.containsKey(features.get(i)[X]))
                continue;
            else
                typesX.put(features.get(i)[X], tempCount++);
        }

        HashMap<String, Integer> uniqueActions = new HashMap<String, Integer>();
        tempCount = 0;
        uniqueActions.put(actions.get(0)[0], tempCount++);
        for (int i=1;i<actions.size();i++)
        {
            if (uniqueActions.containsKey(actions.get(i)[0]))
                continue;
            else
                uniqueActions.put(actions.get(i)[0], tempCount++);
        }

        double[][] countMatrix = new double[typesX.size()][uniqueActions.size()];

        for (int i=0;i<typesX.size();i++)
        {
            for (int j=0;j<uniqueActions.size();j++)
            {
                countMatrix[i][j] = 0;
            }
        }
        int r, c;
        for (int i=0;i<features.size();i++)
        {
            r = typesX.get(features.get(i)[X]);
            c = uniqueActions.get(actions.get(i)[0]);

            countMatrix[r][c]++;
        }

        double[] typesXCount = new double[typesX.size()];
        for (int i=0;i<typesX.size();i++) {
            typesXCount[i] = 0;
            for (int j = 0; j < uniqueActions.size(); j++) {
                typesXCount[i] += countMatrix[i][j];
            }
        }


        double result = 0;
        double temp = 0;
        for (int i=0;i<typesX.size();i++)
        {
            temp = 0;
            for (int j = 0; j < uniqueActions.size(); j++)
            {
                if (countMatrix[i][j] != 0 && typesXCount[i] != 0)
                {
                    double t1 = countMatrix[i][j] / typesXCount[i];
                    double t2 = Math.log(t1);
                    double t3 =  Math.log(2);
                    temp +=  -1 * t1 * (t2/t3);
                }
                else
                {
                    temp += 0;
                }
            }
            result += (typesXCount[i]/features.size()) * temp;
        }

        return result;
    }

    public void recordFeatures(float charPosX, float charPosY, float monsterPosX, float monsterPosY, String action)
    {
        int room_width = 190;
        int room_height = 190;
        String distName;

        double dist = Math.sqrt(Math.pow((charPosX-monsterPosX), 2) + Math.pow((charPosY-monsterPosY),2));
        if (dist<=30)
            distName = "close";
        else
            distName = "far";

        boolean flag = (charPosX<room_width && charPosY<room_height);

        StringBuilder sb = new StringBuilder();
        sb.append(distName);
        sb.append(',');
        sb.append(flag);
        sb.append(',');
        sb.append(action);
        sb.append('\n');
        pw.write(sb.toString());


    }

    public StringBuffer readFile(String fileName)
    {
        StringBuffer stringBuffer = new StringBuffer();
/* Followed the example provided here - http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html; Accessed on  */
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            fileReader.close();

        } catch (IOException e) {
            System.out.println("Wrong filename");
            e.printStackTrace();
        }

        return stringBuffer;
    }
/* -------------------------------------------------------------------------------------------------------------------- */

    public static void main(String args[]) throws IOException
    {
        //new DTLearning().getFeatures("trainData.csv");
        new DTLearning().makeDTFromFeatures("features.csv");
    }

}
