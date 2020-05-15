package com.demo.demos.FindU.SearchByWiFi.core;

import android.text.TextUtils;
import android.util.Log;


import com.demo.demos.FindU.SearchByWiFi.core.SQLite.DataBaseItem;
import com.demo.demos.FindU.SearchByWiFi.core.SQLite.DataBaseOperator;
import com.demo.demos.FindU.SearchByWiFi.core.https.MyHttpsRequest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;



public class WifiProbeManager {
    /**
     * 被扫描的所有ip地址
     */
    private List<String> mScanList = new ArrayList<>();
    private String mLocalIp = "" ;

    private MacListListener mListener;
    private DataBaseOperator mOperator;


    public WifiProbeManager() {

        //数据库操作对象
        this.mOperator = DataBaseOperator.getInstance();
        addAllLocalIp();

    }

    /**
     * 把本地的ip 如 192.168.4.22 后面 192.168.4.1 - 255 所有的IP进行扫描 添加到集合中
     */
    private  void addAllLocalIp() {

        if (mLocalIp.equals(getLocalIp())) return;

        mLocalIp = getLocalIp();

        Log.d("wifi-ip", "WifiProbeManager: " + mLocalIp);
        if (TextUtils.isEmpty(mLocalIp))
            return;

        mScanList.clear();
        String netIp = mLocalIp.substring(0, mLocalIp.lastIndexOf(".") + 1);
        for (int i = 1; i < Constant.COUNT; i++)
        {
            mScanList.add(netIp + i);
        }

        mScanList.remove(mLocalIp);
    }

    /**
     * 开始扫描，发包，并将结果发送出去
     * @param listener
     */
    public void startScan(MacListListener listener){

        addAllLocalIp();//每次进来前 先确定一下wifi的地址

        sendQueryPacket(); // 发包


        mListener = listener;
        mListener.macList(getConnectedHotMac());


    }

    /**
     * 发送包进行arp操作
     */
    private void sendQueryPacket() {
        NetBios netBios = null;
        try
        {
            netBios = new NetBios();

            for (int i = 0; i < mScanList.size(); i++)
            {
                netBios.setIp(mScanList.get(i));
                netBios.setPort(Constant.NETBIOS_PORT);
                netBios.send();

            }
            netBios.close();//这里放里面 下面需要捕获到异常
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 从proc/net/arp中读取ip_mac不需要root ，   如果有root权限 可以通过RE管理器去这个文件夹下查看
     */
    private ArrayList<ListItem> getConnectedHotMac() {

        String mac = null;
        String macUrl = null;
        String macDB = null;
        String ip = null;
        ArrayList<ListItem> connectedMac = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                Log.d("arp", line);
                ip = line.substring(0,17).trim();
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    mac = splitted[3];
                    if(mac.matches("..:..:..:..:..:..") && !mac.equals("00:00:00:00:00:00"))
                    {
                        ListItem item = new ListItem();

                        item.setIp(ip);
                        item.setMac(mac);

                        String[] macMsg = mac.split(":");

                        macUrl = macMsg[0]+"-"+macMsg[1]+"-"+macMsg[2]+"-"+macMsg[3]+"-"+macMsg[4]+"-"+macMsg[5];
                        macDB = macMsg[0]+"-"+macMsg[1]+"-"+macMsg[2];

                        //通过MAC地址获得厂商
                        String firm = MyHttpsRequest.httpsRequest("https://mac.51240.com/"+macUrl+"__mac/","GET",null);
                        //item.put("firm",origazation);
                        item.setFirm(firm);
                        //比较是否为常见摄像头厂商
                        DataBaseItem result = mOperator.selectFirmByMac(macDB);
                        //判断是否返回为空
                        if (result.getmMac() == null && result.getmFirm() == null){
                            item.setResult("未知");
                        }else {
                            item.setResult("疑似");
                        }
                        //是就加到显示列表中
                        if (!connectedMac.contains(item)){
                            connectedMac.add(item);
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connectedMac;
    }

    /**
     * 获取设备的ip地址
     */
    private String getLocalIp() {
        String localIp = "";

        try {
            Enumeration<NetworkInterface> en
                    = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface networkInterface = en.nextElement();
                Enumeration<InetAddress> inetAddresses
                        = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() &&
                            inetAddress instanceof Inet4Address) {
                        localIp = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return localIp;
    }

    public static void i(String tag, String msg) {
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，/信息太长,分段打印
        //  把4*1024的MAX字节打印长度改为2001字符数
        int max_str_length = 2001 - tag.length();
        //大于4000时
        while (msg.length() > max_str_length) {
            Log.i(tag, msg.substring(0, max_str_length));
            msg = msg.substring(max_str_length);
        }
        //剩余部分
        Log.i(tag, msg);

    }


    public interface MacListListener{
         void macList(List<ListItem> macList);
    }



}
