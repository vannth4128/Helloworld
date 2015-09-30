package Client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener{
	private JButton send,clear,exit,login,logout;
    private JPanel p_login,p_chat;
    private JTextField nick,nick1,message;
    private JTextArea msg,online;

    private Socket client;
    private DataStream dataStream;
    private DataOutputStream dos;
	private DataInputStream dis;

	public Client(){
		super("Chat group : Client");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exit();
			}
		});
		setSize(600, 400);
		addItem();
		setVisible(true);
	}
//-----[ Tạo giao diện ]--------//
	private void addItem() {
		setLayout(new BorderLayout());

		exit = new JButton("Exit");
		exit.addActionListener(this);
		send = new JButton("Send");
		send.addActionListener(this);
		clear = new JButton("Delete");
		clear.addActionListener(this);
		login= new JButton("Login");
		login.addActionListener(this);
		logout= new JButton("Exit");
		logout.addActionListener(this);

		p_chat = new JPanel();
		p_chat.setLayout(new BorderLayout());

		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		nick = new JTextField(20);
		p1.add(new JLabel("Nick chat : "));
		p1.add(nick);
		p1.add(exit);

		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());

		JPanel p22 = new JPanel();
		p22.setLayout(new FlowLayout(FlowLayout.CENTER));
		p22.add(new JLabel("Online list"));
		p2.add(p22,BorderLayout.NORTH);

		online = new JTextArea(10,10);
		online.setEditable(false);
		p2.add(new JScrollPane(online),BorderLayout.CENTER);
		p2.add(new JLabel("     "),BorderLayout.SOUTH);
		p2.add(new JLabel("     "),BorderLayout.EAST);
		p2.add(new JLabel("     "),BorderLayout.WEST);

		msg = new JTextArea(10,20);
		msg.setEditable(false);

		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.LEFT));
		p3.add(new JLabel("Message"));
		message = new JTextField(30);
		p3.add(message);
		p3.add(send);
		p3.add(clear);

		p_chat.add(new JScrollPane(msg),BorderLayout.CENTER);
		p_chat.add(p1,BorderLayout.NORTH);
		p_chat.add(p2,BorderLayout.EAST);
		p_chat.add(p3,BorderLayout.SOUTH);
		p_chat.add(new JLabel("     "),BorderLayout.WEST);

		p_chat.setVisible(false);
		add(p_chat,BorderLayout.CENTER);
		//-------------------------
		p_login = new JPanel();
		p_login.setLayout(new FlowLayout(FlowLayout.CENTER));
		p_login.add(new JLabel("Nick chat : "));
		nick1=new JTextField(20);
		p_login.add(nick1);
		p_login.add(login);
		p_login.add(logout);

		add(p_login,BorderLayout.NORTH);
	}
//---------[ Socket ]-----------//
	private void go() {
		try {
    // tạo 1 socket với tên là client ...methods khởi tạo là Socket yêu cầu tên của máy chủ và số hiệu cổng cần kết nối
       //ở đây ta thực hiên ngay trên máy của mình  nên có tên là Localhost....or có thể lấy địa chỉ IP của máy. cổng số 2207
	//Phương thức khởi tạo của Socket yêu cầu tên của máy chủ và số hiệu cổng cần kết nối. 
            //Trong ví dụ này chúng ta đã sử dụng chính máy của mình để thực hiện nên tên mặc định sẽ là “localhost”.	
                    
                    client = new Socket("192.168.47.1",2207);
			dos=new DataOutputStream(client.getOutputStream());
			dis=new DataInputStream(client.getInputStream());

			//client.close();
		} catch (Exception e) {
                        JOptionPane.showMessageDialog(this,"connect fail, review connect!","Message Dialog",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		new Client().go();
	}
        // cái này dùng để gõ chữ tiếng việt
	private void sendMSG(String data){
		try {
			dos.writeUTF(data);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String getMSG(){
		String data=null;
		try {
			data=dis.readUTF();
		} catch (IOException e) {
                    JOptionPane.showMessageDialog(send, "error!");
		}
		return data;
	}

	public void getMSG(String msg1, String msg2){
		int stt = Integer.parseInt(msg1);
		switch (stt) {
		// tin nhắn của những người khác
		case 3:
			this.msg.append(msg2);
			break;
		// update danh sách online
		case 4:
			this.online.setText(msg2);
			break;
		// server đóng cửa
		case 5:
			dataStream.stopThread();
			exit();
			break;
		// bổ sung sau
		default:
			break;
		}
	}
//----------------------------------------------
	private void checkSend(String msg){
		if(msg.compareTo("\n")!=0){
			this.msg.append("Me : "+msg);
			sendMSG("1");
			sendMSG(msg);
		}
	}
	private boolean checkLogin(String nick){
		if(nick.compareTo("")==0)
			return false;
		else if(nick.compareTo("0")==0){
			return false;
		}
		else{
			sendMSG(nick);
			int sst = Integer.parseInt(getMSG());
			if(sst==0)
				 return false;
			else return true;
		}
	}

	private void exit(){
		try {
			sendMSG("0");
			dos.close();
			dis.close();
			client.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==exit){
			dataStream.stopThread();
			exit();
		}
		else if(e.getSource()==clear){
			message.setText("");
		}
		else if(e.getSource()==send){
			checkSend(message.getText()+"\n");
			message.setText("");
		}
		else if(e.getSource()==login){
			if(checkLogin(nick1.getText())){
				p_chat.setVisible(true);
				p_login.setVisible(false);
				nick.setText(nick1.getText());
				nick.setEditable(false);
				this.setTitle(nick1.getText());
				msg.append("Login succesful!\n");
				dataStream = new DataStream(this, this.dis);
			}
			else{
				JOptionPane.showMessageDialog(this,"Existed, input nick new!","Message Dialog",JOptionPane.WARNING_MESSAGE);
			}
		}
		else if(e.getSource()==logout){
			exit();
		}
	}


}
