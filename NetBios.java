package com.demo.demos.FindU.SearchByWiFi.core;


import java.io.IOException;


/**

 */
public class NetBios extends UdpCommunicate
{
    private static final String tag = NetBios.class.getSimpleName();
    private String mIP;
    private int mPort;

    public NetBios(String ip) throws IOException
    {
        super();
        this.mIP = ip;
        this.mPort = Constant.NETBIOS_PORT;
    }

    public NetBios() throws IOException
    {
        super();
    }

    public void setIp(String ip)
    {
        this.mIP = ip;
    }

    public void setPort(int port)
    {
        this.mPort = port;
    }

    @Override
    public String getPeerIp()
    {
        return mIP;
    }

    @Override
    public int getPort()
    {
        return mPort;
    }

//        * 询问包结构:
//        * Transaction ID 两字节（16位） 0x00 0x00
//        * Flags 两字节（16位） 0x00 0x10
//        * Questions 两字节（16位） 0x00 0x01
//        * AnswerRRs 两字节（16位） 0x00 0x00
//        * AuthorityRRs 两字节（16位） 0x00 0x00
//        * AdditionalRRs 两字节（16位） 0x00 0x00
//        * Name:array [12..45] 0x20 0x43 0x4B 0x41(30个) 0x00 ;
//        * Type:NBSTAT 两字节 0x00 0x21
//        * Class:INET 两字节（16位）0x00 0x01
    @Override
    public byte[] getSendContent()
    {

        byte[] t_ns = new byte[50];
        t_ns[0] = 0x00;
        t_ns[1] = 0x00;
        t_ns[2] = 0x00;
        t_ns[3] = 0x10;
        t_ns[4] = 0x00;
        t_ns[5] = 0x01;
        t_ns[6] = 0x00;
        t_ns[7] = 0x00;
        t_ns[8] = 0x00;
        t_ns[9] = 0x00;
        t_ns[10] = 0x00;
        t_ns[11] = 0x00;
        t_ns[12] = 0x20;
        t_ns[13] = 0x43;
        t_ns[14] = 0x4B;
        for (int i = 15; i < 45; i++)
        {
            t_ns[i] = 0x41;
        }
        t_ns[45] = 0x00;
        t_ns[46] = 0x00;
        t_ns[47] = 0x21;
        t_ns[48] = 0x00;
        t_ns[49] = 0x01;
        return t_ns;
    }

}
