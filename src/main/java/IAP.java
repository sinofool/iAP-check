import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

public class IAP {

    public static final Map<Character, Integer> HEX_VALUE_MAP = new HashMap<Character, Integer>();
    static {
        HEX_VALUE_MAP.put('0', 0);
        HEX_VALUE_MAP.put('1', 1);
        HEX_VALUE_MAP.put('2', 2);
        HEX_VALUE_MAP.put('3', 3);
        HEX_VALUE_MAP.put('4', 4);
        HEX_VALUE_MAP.put('5', 5);
        HEX_VALUE_MAP.put('6', 6);
        HEX_VALUE_MAP.put('7', 7);
        HEX_VALUE_MAP.put('8', 8);
        HEX_VALUE_MAP.put('9', 9);
        HEX_VALUE_MAP.put('a', 10);
        HEX_VALUE_MAP.put('b', 11);
        HEX_VALUE_MAP.put('c', 12);
        HEX_VALUE_MAP.put('d', 13);
        HEX_VALUE_MAP.put('e', 14);
        HEX_VALUE_MAP.put('f', 15);
    }

    private static String hexToString(String src) {
        StringBuffer ret = new StringBuffer();
        boolean isPart = false;
        char currentChar = 0;
        for (int i = 0; i < src.length(); ++i) {
            char c = src.charAt(i);
            if (c == '<' || c == '>' || c == ' ' || c == '"') {
                continue;
            }
            if (isPart) {
                currentChar = (char) ((HEX_VALUE_MAP.get(currentChar) << 4) | HEX_VALUE_MAP.get(c));
                ret.append(currentChar);
                isPart = false;
            } else {
                currentChar = c;
                isPart = true;
            }
        }
        return ret.toString();
    }

    /**
     * @param args
     * @throws IOException
     * @throws JSONException
     */
    public static void main(String[] args) throws IOException, JSONException {

        for (String arg : args) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arg)));

            String line = reader.readLine();
            while (line != null) {
                try {
                    int dataPos = line.indexOf("receiptData: ");
                    String data = line.substring(dataPos + "receiptData: ".length());
                    // System.out.println(data);

                    String receipt = new String(Base64.encodeBase64((hexToString(data).getBytes("UTF-8"))));
                    // System.out.println(receipt);

                    String json = new String(Base64.decodeBase64(receipt.getBytes()));
                    // System.out.println(json);
                    JSONObject jsonObj = new JSONObject(json);
                    String purchaseInfo = jsonObj.getString("purchase-info");
                    // System.out.println(purchaseInfo);

                    String transaction = new String(Base64.decodeBase64(purchaseInfo.getBytes()));
                    JSONObject transactionObj = new JSONObject(transaction);
                    // System.out.println(transaction);
                    long transactionId = transactionObj.getLong("transaction-id");
                    System.out.println(transactionId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    line = reader.readLine();
                }
            }

            reader.close();
        }
    }

}
