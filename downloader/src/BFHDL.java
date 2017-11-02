import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BFHDL {
    private Document doc;
    private URL url;
    private String completeVersion = "";

    public static void main(String[] args) {
        System.out.println("* * * RoastToasty/BFH Website Auto-Update Service * * *");
        System.out.println("");
        BFHDL run = new BFHDL();
        run.run();
    }

    private String getFromPattern(String[] patterns, String theText) {
        Pattern pattern = Pattern.compile(Pattern.quote(patterns[0]) + "(.*?)" + Pattern.quote(patterns[1]));
        Matcher m = pattern.matcher(theText);
        String whatToReturn = "";
        while (m.find())
            whatToReturn = m.group(1);
        return whatToReturn;
    }

    private String[] soup2string(ArrayList<String> elms) {
        Object[] objArray = elms.toArray();
        String[] strArray = new String[objArray.length];
        for (int i = 0; i < strArray.length; i++)
            strArray[i] = objArray[i].toString();
        return strArray;
    }

    private void run() {
        ArrayList<String> versions = new ArrayList<>();
        System.out.println("Retrieving info from GitHub...");
        try {
            url = new URL("https://github.com/FaceyComputers/BFH-Android-New/tree/master/Builds/Signed");
        } catch (Exception localException1) {
            System.err.println(localException1.getMessage());
        }
        try {
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String content = "";
            String line;
            while ((line = br.readLine()) != null) {
                content = content.concat(line + "\n");
            }

            doc = Jsoup.parse(content);
        } catch (Exception localException2) {
            System.err.print("ERROR: " + localException2.getMessage());
        }
        Elements buildsElms = doc.select("tr.js-navigation-item");
        String[] pattern = {"<a href=\"/FaceyComputers/BFH-Android-New/blob/master/Builds/Signed/release", ".apk\""};
        for (Element elm : buildsElms) {
            String versiontmp = getFromPattern(pattern, elm.toString());
            versions.add(versiontmp);
        }
        ArrayList<String> major = new ArrayList<>();
        ArrayList<String> minor = new ArrayList<>();
        ArrayList<String> patch = new ArrayList<>();

        for (String ver : soup2string(versions)) {
            if (!ver.isEmpty()) {
                String[] tmpver = ver.split("\\.");
                major.add(tmpver[0]);
                minor.add(tmpver[1]);
                patch.add(tmpver[2]);
            }
        }

        for (int a = 5; a > -1; a--) {
            if (major.contains(Integer.toString(a))) {
                for (int b = 500; b > -1; b--) {
                    if (minor.contains(Integer.toString(b))) {
                        for (int c = 10000; c > -1; c--) {
                            if (patch.contains(Integer.toString(c))) {
                                completeVersion = (Integer.toString(a) + "." + Integer.toString(b) + "." + Integer.toString(c));
                                if (doc.toString().contains(completeVersion)) {
                                    c = -1;
                                    System.out.println(completeVersion);
                                }
                            }
                        }
                        b = -1;
                    }
                }
                a = -1;
            }
        }
        System.out.println("Done.");
        String newUrlStr = "https://github.com/FaceyComputers/BFH-Android-New/raw/master/Builds/Signed/release" + completeVersion + ".apk";
        try {
            System.out.println("Updating HTML with latest version...");
            FileInputStream fis = new FileInputStream("/var/www/html/template.html");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader buff = new BufferedReader(isr);
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = buff.readLine()) != null) {
                lines.add(line);
            }
            String finalString = "";
            for (String line1 : lines) {
                finalString = finalString.concat("\n" + line1);
            }
            finalString = finalString.replace(",,,", completeVersion);
            PrintWriter out = new PrintWriter("/var/www/html/index.html");
            out.println(finalString);
            System.out.println("Done.");
            System.out.println("Downloading latest APK from GitHub...");
            URL newUrl = new URL(newUrlStr);
            ReadableByteChannel rbc = java.nio.channels.Channels.newChannel(newUrl.openStream());
            FileOutputStream fos = new FileOutputStream("/var/www/html/bfh-app/bfh-latest.apk");
            fos.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
            System.out.println("Done.");
            System.out.println("Restarting lighttpd service...");
            Runtime.getRuntime().exec("sudo service lighttpd restart");
            System.out.println("Done.");
            out.close();
            fos.close();
            buff.close();
            System.out.println("\nAll operations completed successfully.");
            Thread.sleep(2000);
        } catch (Exception ex) {
            System.err.println("Failed: " + ex.getMessage());
        }
    }
}
