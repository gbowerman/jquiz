
// class for a set of quiz questions and answers
import java.util.ArrayList;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

public class subject {
    private String title;
    private ArrayList<String> questions;
    private ArrayList<String> answers;
    private Random ran = new Random();
    private int nextQuestion = 0;
    private boolean deleteMode = true;

    public subject(String fileName) {
        questions = new ArrayList<>();
        answers = new ArrayList<>();

        // open the quiz file
        FileReader quizFile = null;
        String quizTitle = "";
        try {
            quizFile = new FileReader(fileName, StandardCharsets.UTF_8);
            BufferedReader buffer = new BufferedReader(quizFile);
            quizTitle = buffer.readLine();
            // initialize a new quiz
            this.title = quizTitle;

            // load the questions & answers
            String qaLine = "";
            while ((qaLine = buffer.readLine()) != null) {
                // System.out.println(qaLine);
                String[] qa = qaLine.split("\\|");
                this.addQA(qa[0], qa[1]);
            }
            buffer.close();
            quizFile.close();
        } catch (Exception e) {
            System.out.println("File not found.");
            System.exit(0);
        }
    }

    public void addQA(String question, String answer) {
        questions.add(question);
        answers.add(answer);
    }

    // returns the number of questions and answers
    public int quizLength() {
        return this.questions.size();
    }

    // remove a question/answer from the list (e.g. once answered)
    public void delQuestion() {
        this.questions.remove(this.nextQuestion);
        this.answers.remove(this.nextQuestion);
    }

    // return a question based on index number
    public String getQuestion() {
        this.nextQuestion = ran.nextInt(this.quizLength());
        return this.questions.get(this.nextQuestion);
    }

    // return an answer based on index number
    public String getAnswer() {
        return this.answers.get(this.nextQuestion);
    }

    // return the quiz title
    public String getTitle() {
        return this.title;
    }

    // return true if quiz is in delete mode
    public boolean getDeleteMode() {
        return this.deleteMode;
    }

    // set the delete mode
    public void setDeleteMode(boolean mode) {
        this.deleteMode = mode;
    }
}