// quiz.java - quiz based tool to learn facts in a question/answer format
// to do:
//  - Select subject
//  - make a web app
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.event.ActionEvent;

public class quiz {
    // game state variables and dynamic UI components
    static int score = 0; // number of correct answers
    static int qCount = 0; // number of questions asked
    static subject quizInstance = null;
    static JLabel questionLabel = new JLabel("");
    static JLabel scoreLabel = new JLabel("");
    static JPanel statusPanel = new JPanel();
    static JLabel statusLabel = new JLabel("");
    static JTextField answerField = new JTextField(32);

    public static void main(String[] args) {
        // open the quiz file
        FileReader quizFile = null;
        String quizTitle = "";
        try {
            quizFile = new FileReader("quiz.txt");
            BufferedReader buffer = new BufferedReader(quizFile);
            quizTitle = buffer.readLine();
            // initialize a new quiz
            quizInstance = new subject(quizTitle);

            // load the questions & answers
            String qaLine = "";
            while ((qaLine = buffer.readLine()) != null) {
                // System.out.println(qaLine);
                String[] qa = qaLine.split("\\|");
                quizInstance.addQA(qa[0], qa[1]);
            }
            buffer.close();
            quizFile.close();
        } catch (Exception e) {
            System.out.println("File not found.");
            System.exit(0);
        }

        // initialize the GUI objects
        JFrame frame = createAppFrame(640, 170);

        // Create 3 JPanel objects = header, quiz, status
        JPanel headerPanel = createHeaderPanel(quizTitle);
        JPanel quizPanel = createQuizPanel();
        setStatusPanel();

        // Add the panels to the frame
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(quizPanel, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Make the frame visible
        frame.setVisible(true);
        // set focus to the answer field
        answerField.requestFocusInWindow();

        // start quiz
        String nextQuestion = quizInstance.getQuestion();
        questionLabel.setText(nextQuestion);        
    }

    /**
     * evaluate() - the main question/answer evaluation method
     * 
     * @param responseField
     */
    static void evaluate(JTextField responseField) {
        String answer = quizInstance.getAnswer();
        String response = responseField.getText();
        if (answer.equalsIgnoreCase(response)) {
            score++;
            statusPanel.setBackground(Color.green);
            statusLabel.setText("Correct");
            // if delete mode is on, remove the question
            if (quizInstance.deleteMode == true) {
                quizInstance.delQuestion(quizInstance.nextQuestion);
            }
        } else {
            statusPanel.setBackground(Color.orange);
            statusLabel.setText("Answer is " + answer);
            System.out.printf("\nanswer:%s,response:%s.", answer, response);
        }
        qCount++;
        int scorePercent = 100 * score / qCount;
        scoreLabel.setText(score + "/" + qCount + " " + scorePercent + "%");
        responseField.setText("");
        if (quizInstance.deleteMode == true) {
            // if no more questions left, end the quiz
            if (quizInstance.quizLength() == 0) {
                // show a dialog box with the score
                questionLabel.setText("Game over");
                JOptionPane.showMessageDialog(null, "You scored " + score + "/" + qCount + " (" + scorePercent + "%)",
                        "End of quiz", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
        String nextQuestion = quizInstance.getQuestion();
        questionLabel.setText(nextQuestion);
    }

    // GUI components
    static JFrame createAppFrame(int width, int height) {
        JFrame frame = new JFrame("Quiz flash cards");
        // Set the size of the frame
        frame.setSize(width, height);
        // Set the layout of the frame to BorderLayout
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    // create and initialize the head panel
    /**
     * @param title
     * @return
     */
    static JPanel createHeaderPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.lightGray);
        panel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel();
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setText(title + "      ");
        // GUI components for delete mode
        JCheckBox deleteMode = new JCheckBox("Delete mode");
        // set the checkbox as selected by default
        deleteMode.setSelected(true);
        Action deleteAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (deleteMode.isSelected()) {
                    quizInstance.deleteMode = true;
                } else {
                    quizInstance.deleteMode = false;
                }
            }
        };
        deleteMode.addActionListener(deleteAction);
        deleteMode.setBackground(Color.lightGray);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(deleteMode, BorderLayout.EAST);
        scoreLabel.setFont(new Font("Serif", Font.BOLD, 20));
        return panel;
    }

    // create and initialize the main quiz panel
    static JPanel createQuizPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        SpringLayout layout = new SpringLayout();  
        panel.setLayout(layout);
        answerField.setFont(new Font("Serif", Font.BOLD, 20));
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                evaluate(answerField);
            }
        };
        answerField.addActionListener(action);
        questionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        JButton enterButton = new JButton("Enter");
        enterButton.setFont(new Font("Serif", Font.BOLD, 20));
        // set the height of the button
        enterButton.setPreferredSize(new Dimension(90, 30));
        enterButton.addActionListener(action);
        panel.add(questionLabel);
        panel.add(answerField);
        panel.add(enterButton);
        layout.putConstraint(SpringLayout.WEST, questionLabel,6,SpringLayout.WEST, panel);  
        layout.putConstraint(SpringLayout.NORTH, questionLabel,6,SpringLayout.NORTH, panel);  
        layout.putConstraint(SpringLayout.WEST, answerField,6,SpringLayout.WEST, panel);  
        layout.putConstraint(SpringLayout.NORTH, answerField,6,SpringLayout.SOUTH, questionLabel);  
        layout.putConstraint(SpringLayout.WEST, enterButton,6,SpringLayout.EAST, answerField);  
        layout.putConstraint(SpringLayout.NORTH, enterButton,6,SpringLayout.SOUTH, questionLabel);  

        return panel;
    }

    // create a footer panel for status
    static void setStatusPanel() {
        statusPanel.setBackground(Color.lightGray);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusLabel.setFont(new Font("Serif", Font.BOLD, 20));
        statusPanel.add(scoreLabel, BorderLayout.EAST);
    }
}