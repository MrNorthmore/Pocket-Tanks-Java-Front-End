/**
 * Created by jterp on 2017-12-05.
 */
//import javafx.scene.shape.Circle;

//import javafx.scene.shape.Circle;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.OutputStream;
import java.io.InputStream;
import java.lang.Thread;
import java.util.ArrayList;

import javax.swing.*;

//import statements

public class FrontEndMain extends JFrame {

    //Elipse.2D
    private int posx = 10; // m
    private int posy = 230;  // m
    private int myHealth = 100;
    private int OppHealth = 100;
    private int count=0;
    JLabel jlbHelloWorld;
    private int launchAngle = 45;
    private int player = 0;
    private int whosTurn = 1;
    //private Point[] = new Point[];
    private ArrayList<int[]> points = new ArrayList<int[]>();
    //private Circle p = new Circle(10,230,5);
    private OutputStream socketOutput;
    private InputStream socketInput;
    private Socket theSocket;
    PlayingField theField;
    private boolean yourTurn = true;
    private JLabel message = new JLabel("new Game");
    private JLabel angle = new JLabel("50");
    public void SendString(String s)
    {
        try
        {
            socketOutput.write(s.getBytes());
            System.out.println("Sent:"+s);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }

    public class UpButtonListener  implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            System.out.println(yourTurn + "is your turn");
            if (yourTurn){
                int newAngle = Integer.parseInt(angle.getText()) + 3;
                angle.setText(Integer.toString(newAngle));
            }
            //SendString("Up");
        }
    }
    public class RightButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SendString("Right");
        }
    }
    public class DownButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (yourTurn){
                int newAngle = Integer.parseInt(angle.getText()) - 3;
                angle.setText(Integer.toString(newAngle));
            }
            //SendString("Down");
        }
    }
    public class LeftButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SendString("left");
        }
    }
    public class LaunchButtonListener implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            Thread animationThread = new Thread(new Runnable() {
                public void run() {
                    if (yourTurn) {
                        yourTurn = false;
                        int thisangle = Integer.parseInt(angle.getText());
                        if (player == 2) {
                            thisangle = 180 - thisangle;
                            posx = 454;
                            posy = 229;
                        }
                        else{
                            posx = 10; // m
                            posy = 229;  // m
                        }
                        int v0 = 30; // m/s
                        //int angle = 60;
                        double dt = 0.10; // s

                        double vx = v0 * Math.cos(Math.PI / 180 * thisangle);
                        double vy = v0 * Math.sin(Math.PI / 180 * thisangle);


                        double time = 0; // s


                        while (posy < 230) {
                            System.out.println("posx: " + posx + "posy: " + posy);
                            posx += 5 * vx * dt;
                            posy -= vy * dt;
                            time += dt;

                            //  p.move(posx,posy);

                            // change speed in y
                            vy -= 9.82 * dt; // gravity
                            theField.repaint();
                            //repaint();
                            SendString(Integer.toString(posx) + "," + Integer.toString(posy));
                            try {
                                Thread.sleep(100);
                            } catch (Exception ex) {
                            }
                        }

                        if (player ==1) {
                            if (posy>205 && posy<235 && posx<484 && posx >454){
                                OppHealth -=10;
                                theField.repaint();
                                SendString("hitP2");
                            }
                            else{
                                int[] arr = {posx,posy};
                                points.add(count,arr);
                                count++;
                                SendString("miss1");
                                theField.repaint();
                            }
                            SendString("done1");
                        }
                        else{
                            if (posy>205 && posy<235 && posx<30 && posx >0) {
                                OppHealth -= 10;
                                theField.repaint();
                                SendString("hitP1");
                            }
                            else{
                                int[] arr = {posx,posy};
                                points.add(count,arr);
                                count++;
                                SendString("miss2");
                                theField.repaint();

                            }
                            SendString("done2");
                        }

                    }

                }
            });

            //SendString("left");

            animationThread.start();

        }
    }


    public class ConnectionThread extends Thread
    {
        public ConnectionThread()
        {

        }
        public void run()
        {
            try
            {
                byte[] readBuffer = new byte[256];
                for (int i=0;i<256;i++)
                    readBuffer[i]=0;
                while(true)
                {
                    socketInput.read(readBuffer);
                    String decoded = new String(readBuffer, "UTF-8");
                    //char pass = decoded.charAt(0);
                    theField.updateScreen(decoded);//.SwingUtilities.invokeLater();

                    System.out.println("Got:" + decoded);

                }
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
            }
        }
    }
    ConnectionThread theConnection;
    public class ConnectButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String one = new String("hello");
            String two = new String("world");
            try
            {
                theSocket = new Socket(InetAddress.getByName("127.0.0.1"),2000);
                socketOutput = theSocket.getOutputStream();
                socketInput = theSocket.getInputStream();
                theConnection = new ConnectionThread();
                theConnection.start();
                System.out.println("connect");
            }
            catch(Exception ex)
            {
                System.out.println(ex.toString());
            }
        }
    }
    public class DisconnectButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                theSocket.close();
            }
            catch(Exception ex)
            {
                System.out.println(ex.toString());
            }
            System.out.println("disconnect");
        }
    }
    public class PlayingField extends Canvas
    {
        public void updateScreen (String option){
            System.out.println("waiting" + option);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    System.out.println ("in run");
                    System.out.println(option);
                    char comp = option.charAt(0);
                    String one = "1";
                    System.out.println(comp == '1');
                    //String str = option.toString();
                    if (comp == '1'){
                        player = 1;
                        yourTurn = true;
                        System.out.println("waiting");
                        message.setText("Waiting for player 2 to join");

                    }
                    else if(comp == '2'){
                        if (player!=1){
                            player = 2;
                            yourTurn = false;
                        }
                        System.out.println("Game Begin!");
                        message.setText("Game Start!");
                    }
                    else if (comp == 'p'){
                        System.out.println("they have fired");
                        String[] coord = option.split(",");
                        System.out.println(coord[1]+" "+coord[2]);
                        posx = Integer.parseInt(coord[1].trim());
                        posy = Integer.parseInt(coord[2].trim());
                        theField.repaint();
//                        ArrayList<String> coord = Arrays.asList(option.split(","));
//                        posx = coord.;
//                        posy = coord[1];
                    }
                    else if (comp == '3'){
                        System.out.println("should be in here when 1 finished");
                        if (player == 1){
                            yourTurn =false;

                        }
                        else{

                            yourTurn = true;
                        }
                    }
                    else if (comp == '4'){
                        System.out.println("should be in here when 2 finished");
                        if (player == 2){
                            yourTurn =false;

                        }
                        else {
                            yourTurn = true;
                        }
                    }
                    else if(comp == '5'){
                        if (player == 2){
                            myHealth-=10;
                            theField.repaint();
                        }
                    }
                    else if (comp == '6'){
                        if (player ==1){
                            myHealth-=10;
                            theField.repaint();
                        }
                    }
                    else if (comp =='7'){
                        if (player == 2){
                            int[] arr = {posx,posy};
                            points.add(arr);
                            theField.repaint();
                        }
                    }
                    else if(comp == '8'){
                        if (player ==1){
                            int[] arr = {posx,posy};
                            points.add(arr);
                            theField.repaint();
                        }
                    }
                   // else if (option == )
                    //theField.repaint();
                }
//                public void option1(){
//
//                }

            });
        }

        Dimension theDimension;
        Dimension minDim;
        public PlayingField (Dimension d)
        {
            theDimension = d;
            minDim = new Dimension();
            minDim.width = d.width/2;
            minDim.height = d.height/2;
        }

        public Dimension getPreferredSize()
        {
            return theDimension;
        }
        public Dimension getMinimumSize()
        {
            return minDim;
        }
        public void paint(Graphics g)
        {

            g.setColor(Color.black);
            Rectangle bounds= getBounds();
            System.out.println(bounds.y);
            System.out.println(bounds.x);
            System.out.println(bounds.width);
            System.out.println(bounds.height);


            g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);

            g.setColor(Color.YELLOW);
            if (player ==1) {
                g.drawString("PlayerOne Health: ", 10, 10);
                g.drawString(Integer.toString(myHealth), 10, 25);
                g.drawString("PlayerTwo Health: ", 384, 10);
                g.drawString(Integer.toString(OppHealth), 424, 25);
            }

            if (player ==2) {
                g.drawString("PlayerOne Health: ", 10, 10);
                g.drawString(Integer.toString(OppHealth), 10, 25);
                g.drawString("PlayerTwo Health: ", 384, 10);
                g.drawString(Integer.toString(myHealth), 424, 25);
            }


            Rectangle R = new Rectangle(0,230,484,230);
            g.setColor (Color.red);
            g.fillOval(10,220,20,20);

            g.setColor (Color.blue);
            g.fillOval(454,220,20,20);

            g.setColor(Color.white);
            g.fillOval(posx,posy, 10, 10);

            g.setColor (Color.green);
            g.fillRect(0,230,bounds.width,230);

            g.setColor(Color.black);
            for (int i=0; i<points.size(); i++){
                g.fillOval(points.get(i)[0],points.get(i)[1] - 5,20,20);
            }

            //g.setColor(Color.white);
            System.out.println(posx);
            System.out.println(posy);

            //g.fillOval(posx,posy, 10, 10);

           // g.
//            message.setText("fdsafdsafdsaf");
//            message.setLocation(20,30);

            // Circle tank1 = new Circle(10,230,10);
         //   setVisible(true);


        }
    }
    public static void main(String args[])
    {
        new FrontEndMain();
    }
    FrontEndMain()
    {

        jlbHelloWorld = new JLabel("Hello World");
        JButton launchButton = new JButton ("Launch");
        launchButton.addActionListener(new LaunchButtonListener());
        launchButton.setSize(20,20);
        JButton upButton = new JButton("Up");
        upButton.addActionListener(new UpButtonListener());
        upButton.setSize(20,20);
//        JButton rightButton = new JButton("Right");
//        rightButton.addActionListener(new RightButtonListener());
 //       rightButton.setSize(100,20);
        JButton downButton = new JButton("Down");
        downButton.addActionListener(new DownButtonListener());
        downButton.setSize(100,20);
//        JButton leftButton = new JButton("Left");
//        leftButton.addActionListener(new LeftButtonListener());
//        leftButton.setSize(100,20);

        JPanel directionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2; c.gridy = 1;
        directionPanel.add(upButton,c);
//        c.gridx = 1; c.gridy = 0;
//        directionPanel.add(rightButton,c);
        c.gridx = 2; c.gridy = 3;
        directionPanel.add(downButton,c);
//        c.gridx = 2; c.gridy = 1;
//        directionPanel.add(leftButton,c);
        c.gridx = 4; c.gridy = 3;
        directionPanel.add(launchButton,c);

        c.gridx = 4;c.gridy = 1;
        directionPanel.add(angle,c);

        Dimension d = new Dimension();
        d.width = 500;
        d.height = 450;
        theField = new PlayingField(d);

        JPanel connectionPanel = new JPanel(new GridBagLayout());
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ConnectButtonListener());
        c.gridx = 0; c.gridy = 0;
        connectionPanel.add(connectButton,c);
        JButton disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(new DisconnectButtonListener());
        c.gridx = 1; c.gridy = 0;
        connectionPanel.add(disconnectButton,c);
        c.gridx = 1; c.gridy = 1;
        connectionPanel.add(message ,c);

        Rectangle theBounds = getBounds();


        /*setLayout(new GridLayout(2,2));
        add(theField);
        add(connectionPanel);
        add(directionPanel);*/
        setLayout(new GridBagLayout());
        c.gridx = 0; c.gridy = 0;
        c.gridheight = 9;
        c.gridwidth = 10;
        c.weightx = 1.0;c.weighty=1.0;
        c.fill = GridBagConstraints.BOTH;
        add(theField,c);
        c.gridx = 0; c.gridy = 9;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        add(connectionPanel,c);
        c.gridx = 7;c.gridy = 9;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        add(directionPanel,c);
        this.setSize(500,500);
        this.setTitle("Network Game Console");

        setVisible(true);
    }
}
