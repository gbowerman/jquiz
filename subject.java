// class for a set of quiz questions and answers
import java.util.ArrayList;
import java.util.*;

public class subject {
    String title;
    ArrayList<String> questions;
    ArrayList<String> answers;
    Random ran = new Random();
    int nextQuestion = 0;

    public subject(String title) {
        this.title = title;
        questions = new ArrayList<>();
        answers = new ArrayList<>();
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
    public void delQuestion(int index) {
        if (index >= this.quizLength()) {
            System.out.printf("\nError: delQuestion invalid index %d\n", index);
            return;
        }
        this.questions.remove(index);
        this.answers.remove(index);
    }

    // return a question based on index number
    public String getQuestion() {
        nextQuestion = ran.nextInt(this.quizLength());
        return this.questions.get(nextQuestion);
    }

    // return an answer based on index number
    public String getAnswer() {
        return this.answers.get(nextQuestion);
    }
}