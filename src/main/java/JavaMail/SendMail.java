package JavaMail;

/**
 * Created by yansh on 17-9-14.
 */
import java.util.Properties;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

    //需要修改的内容都放在此处

    //第几次作业
    static String time = "1";

    //smtp服务器的地址,此处是sjtu邮箱的smtp服务器的地址.163邮箱在发送邮件封数高的时候会直接判定垃圾邮件,不建议使用,其他未测试.
    static String smtp = "mail.sjtu.edu.cn";
    //邮箱账户&密码
    static String userName = "yansh0625@sjtu.edu.cn";
    static String password = "##";

    // 成绩文件仅支持csv格式.成绩文件放在项目最顶层目录下(见当前的hw1.csv的位置),
    // 目前仅支持以','分隔的文件.保存成绩文件时直接保存以','分隔的csv文件即可
    // 文件内格式请直接查看当前hw1.csv的内容格式,可在此基础上修改.(第一项一定要是邮件地址,最后一项即评语可为空)
    static String graphFile = "hw1.csv";
    //邮件标题
    static String title = "软件工程导论第"+time+"次成绩通知";
    //邮件内容(以成绩为界分为前后两块)
    static String front = "同学你好！<br>" +
            "以下是你在软件工程导论课程提交的第"+time+"次作业的成绩，特此通知。<br>";
    static String end = "如果对本成绩有异议，请通过QQ与助教联系。<br>" +
            "请勿直接回复该邮件咨询成绩.<br>"+
            "祝好！";

    //测试模式:将test设置为true进入test模式,将会以成绩文件第一行的学生信息为内容发送一封邮件至测试邮箱testSend,便于查看发送效果.
    static boolean test = true;
    //测试邮箱
    static String testSend = "tumifh@163.com";

    //注意事项
    // 每一封邮件发送成功后console都会输出"学号+OK".
    // 在目标邮箱地址不正确的时候有可能console也会出现OK,但一定会发送一封退信给发件邮箱.此时想要补发邮件时可以修改graphFile,或者在test模式下修改testSend再发送即可.
    // 发出的邮件可能会延迟一小段时间,也有可能被归类垃圾邮件,请提醒学生查看.

    //以下不需修改
    public static String generalContent(String[] item,String[] itemName){
        String graph = "";
        for(int i = 1;i<item.length;i++){
            graph+=itemName[i]+":"+item[i]+"<br>";
        }
        return front + "<br>" + graph+"<br>" +end;
    }

    public static void sendEmail(String[] str,String[]itemName,String toUser,MimeMessage message){
        try {
            InternetAddress to = new InternetAddress(toUser);
            message.setRecipient(RecipientType.TO, to);
            message.setSubject(title);
            message.setContent(generalContent(str,itemName), "text/html;charset=UTF-8");
            Transport.send(message);
            System.out.print(str[1]+",OK\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MessagingException {
        final Properties props = new Properties();

        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", smtp);
        // 发件人的账号
        props.put("mail.user", userName);
        // 访问SMTP服务时需要提供的密码
        props.put("mail.password", password);

        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress form = new InternetAddress(
                props.getProperty("mail.user"));
        message.setFrom(form);
        File file = new File(graphFile);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            if (test==true){//测试模式
                tempString = reader.readLine();
                String itemName[]=tempString.split(",");
                tempString = reader.readLine();
                sendEmail(tempString.split(","),itemName,testSend,message);
            }
            else{//非测试模式
                tempString = reader.readLine();
                String itemName[]=tempString.split(",");
                while ((tempString = reader.readLine()) != null) {
                    String str[]=tempString.split(",");
                    sendEmail(str,itemName,str[0],message);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
