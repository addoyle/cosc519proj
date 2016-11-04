import java.io.*;
import java.util.*;
class process {
    String name;
    int bt;
}
class RR {
    public static void main(String s[]) throws IOException {
        process p[] = new process[4];
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int e = 0;
        for (int i = 0; i <= 3; i++) {
            p[i] = new process();
            System.out.println("process");
            p[i].name = br.readLine();
            System.out.println("Enter the burst time");
            p[i].bt = Integer.parseInt(br.readLine());
            e += p[i].bt;



        }
        System.out.println();
        int t = 0, time = 0;
        System.out.println("Enter the quantum time:");
        t = Integer.parseInt(br.readLine());
        System.out.println("\n Starting Processes \n");
        while (time < e) {
            for (int i = 0; i <= 3; i++) { if (p[i].bt != 0) { if (p[i].bt >= t) {
                        time += t;
                        p[i].bt -= t;
                        System.out.println("Process " + p[i].name + " Burst Time Remaining is " + p[i].bt + " After Time " + time);
                    } else {
                        time += p[i].bt;
                        p[i].bt = 0;
                        System.out.println(" Process " + p[i].name + "is completed " + time);
                    }
                }
            }
        }
    }
}