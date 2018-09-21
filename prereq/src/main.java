import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/*
    https://kth.kattis.com/problems/kth.ai.sokobanpathfinding

    ’ ’ - Free space
    ’#’ - Wall
    ’.’ - Goal
    ’@’ - Sokoban player
    ’+’ - Sokoban player on goal
    ’$’ - Box
    ’*’ - Box on goal
*/

public class main {

    public static void main(String[] args) throws FileNotFoundException {
        File filepath = new File("./resources/sample1.txt");
        Scanner sc = new Scanner(filepath);

        ArrayList map = new ArrayList<String[]>();

        int x;
        int y;
        int tempY=0;
        boolean positionFound = false;
        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            if (!positionFound && str.indexOf('@') >= 0) {
                x = str.indexOf('@');
                y = tempY;
                positionFound = true;
            }
            String[] row = (str.split(""));
            map.add(row);
            tempY++;
        }


        for (Object aMap : map) {
            String[] currRow = (String[]) aMap;
            for (String aCurrRow : currRow) {
                System.out.print(aCurrRow);
            }
            System.out.print('\n');
        }

    }
}
