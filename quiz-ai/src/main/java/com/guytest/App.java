package com.guytest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Quiz program with Azure openai Info button
 *
 */
public class App {
    // game state variables and dynamic UI components
    static int score = 0; // number of correct answers
    static int qCount = 0; // number of questions asked
    static subject quizInstance = null;
    static JLabel questionLabel = new JLabel("");
    static JLabel scoreLabel = new JLabel("0/0 0%");
    static JPanel statusPanel = new JPanel();
    static JLabel statusLabel = new JLabel("<Enter the first answer>");
    static JTextField answerField = new JTextField(32);
    static JButton infoButton;
    static String nextQuestion = "";
    static String quizTitle = null;
    static String answer = "anything"; // Info button uses answer, so set it to anything if no question yet
    static String lastQuestion = "";
    static quizailib quizaiinst = null;

    public static void main(String[] args) {
        // initialize the quiz
        quizInstance = new subject("quiz.txt");
        quizTitle = quizInstance.getTitle();

        // initialize a quizailib object
        quizaiinst = new quizailib();

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
        nextQuestion = quizInstance.getQuestion();
        lastQuestion = nextQuestion; // initialize the lastQuestion variable to use for the AI Info prompt
        questionLabel.setText(nextQuestion);
    }

    /**
     * evaluate() - the main question/answer evaluation method
     * 
     * @param responseField
     */
    static void evaluate(JTextField responseField) {
        answer = quizInstance.getAnswer();
        lastQuestion = nextQuestion; // setting the lastQuestion variable to use for the AI Info prompt
        String response = responseField.getText();
        if (answer.equalsIgnoreCase(response)) { // right answer
            score++;
            statusPanel.setBackground(Color.green);
            statusLabel.setText("Correct");
            // if delete mode is on, remove the question
            if (quizInstance.getDeleteMode() == true) {
                quizInstance.delQuestion();
            }
        } else { // wrong answer
            statusPanel.setBackground(Color.orange);
            statusLabel.setText("Answer is " + answer);
            System.out.printf("\nanswer:%s,response:%s.", answer, response);
        }
        qCount++;
        int scorePercent = 100 * score / qCount;
        scoreLabel.setText(score + "/" + qCount + " " + scorePercent + "%");
        responseField.setText("");
        if (quizInstance.getDeleteMode() == true) {
            // if no more questions left, end the quiz
            if (quizInstance.quizLength() == 0) {
                // show a dialog box with the score
                questionLabel.setText("Game over");
                JOptionPane.showMessageDialog(null, "You scored " + score + "/" + qCount + " (" + scorePercent + "%)",
                        "End of quiz", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
        nextQuestion = quizInstance.getQuestion();
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
                    quizInstance.setDeleteMode(true);
                } else {
                    quizInstance.setDeleteMode(false);
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

        // set an abstract action for the Info button
        Action infoAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // sent the prompt and get the response from OpenAI
                String response = quizaiinst.getAIResponse(lastQuestion, answer);
                JOptionPane.showMessageDialog(null, response, "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
        // add an "Info" button to the status panel
        infoButton = new JButton("Info");
        infoButton.setFont(new Font("Serif", Font.BOLD, 20));
        infoButton.setPreferredSize(new Dimension(90, 30));
        infoButton.addActionListener(infoAction);
        // add the button to the middle of the status panel
        // statusPanel.add(infoButton, BorderLayout.CENTER);

        panel.add(questionLabel);
        panel.add(answerField);
        panel.add(enterButton);
        panel.add(infoButton);
        layout.putConstraint(SpringLayout.WEST, questionLabel, 6, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, questionLabel, 6, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, infoButton, 6, SpringLayout.EAST, answerField);
        layout.putConstraint(SpringLayout.NORTH, infoButton, 2, SpringLayout.NORTH, questionLabel);
        layout.putConstraint(SpringLayout.WEST, answerField, 6, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, answerField, 6, SpringLayout.SOUTH, questionLabel);
        layout.putConstraint(SpringLayout.WEST, enterButton, 6, SpringLayout.EAST, answerField);
        layout.putConstraint(SpringLayout.NORTH, enterButton, 6, SpringLayout.SOUTH, questionLabel);

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
