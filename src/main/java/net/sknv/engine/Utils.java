package net.sknv.engine;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String filename) throws Exception{
        String result;
        try(InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(filename);
                Scanner scanner = new Scanner(in, "UTF-8")){
            result = scanner.useDelimiter("\\A").next();
        }

        return result;
    }

    public static List<String> readAllLines(String fileName) throws IOException, ClassNotFoundException {
        List<String> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)));
        String line;
        while ((line = br.readLine()) != null) {
            list.add(line);
        }
        return list;
    }

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static Vector3f getEulerAngles(Quaternionf q){
        Vector3f eulerAngles = new Vector3f();
        eulerAngles.x = (float) Math.atan2(2.0 * (q.x*q.w - q.y*q.z), 1.0 - 2.0 * (q.x*q.x + q.y*q.y));
        float f = (float) (2.0 * (q.x*q.z + q.y*q.w));
        f = f>1 ? 1 : (f < -1 ? -1 : f);
        eulerAngles.y = (float) Math.asin(f);
        eulerAngles.z = (float) Math.atan2(2.0 * (q.z*q.w - q.x*q.y), 1.0 - 2.0 * (q.y*q.y + q.z*q.z));
        return eulerAngles;
    }
}
