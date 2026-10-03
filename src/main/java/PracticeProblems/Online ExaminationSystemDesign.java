import java.util.*;

public class Question1_OnlineExaminationSystem {

    // Abstraction: different question types can implement this interface.
    interface Question {
        String getQuestionText();
        boolean isCorrect(String answer);
    }

    static class MultipleChoiceQuestion implements Question {
        private final String text;
        private final String correctAnswer;

        MultipleChoiceQuestion(String text, String correctAnswer) {
            this.text = text;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestionText() {
            return text;
        }

        public boolean isCorrect(String answer) {
            return correctAnswer.equalsIgnoreCase(answer);
        }
    }

    static class TrueFalseQuestion implements Question {
        private final String text;
        private final boolean correctAnswer;

        TrueFalseQuestion(String text, boolean correctAnswer) {
            this.text = text;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestionText() {
            return text;
        }

        public boolean isCorrect(String answer) {
            return Boolean.parseBoolean(answer) == correctAnswer;
        }
    }

    static class Student {
        private final String name;

        Student(String name) {
            this.name = name;
        }
    }

    static class Examination {
        private final String title;
        private final List<Question> questions = new ArrayList<>();

        Examination(String title) {
            this.title = title;
        }

        void addQuestion(Question question) {
            questions.add(question);
        }

        Attempt startAttempt(Student student) {
            return new Attempt(student, this, questions);
        }

        String getTitle() {
            return title;
        }
    }

    static class Attempt {
        private final Student student;
        private final Examination examination;
        private final List<Question> questions;
        private final Map<Integer, String> answers = new HashMap<>();
        private boolean submitted = false;

        Attempt(Student student, Examination examination, List<Question> questions) {
            this.student = student;
            this.examination = examination;
            this.questions = new ArrayList<>(questions);
        }

        void answerQuestion(int questionNumber, String answer) {
            if (submitted) {
                System.out.println("Cannot change answer: Attempt already submitted.");
                return;
            }

            if (questionNumber < 1 || questionNumber > questions.size()) {
                System.out.println("Invalid question number.");
                return;
            }

            answers.put(questionNumber, answer);
            System.out.println("Question " + questionNumber +
                    " answered with '" + answer + "'.");
        }

        void submit() {
            if (submitted) {
                System.out.println("Attempt has already been submitted.");
                return;
            }

            submitted = true;
            int correct = 0;

            for (int i = 0; i < questions.size(); i++) {
                String answer = answers.get(i + 1);
                if (answer != null && questions.get(i).isCorrect(answer)) {
                    correct++;
                }
            }

            System.out.println("Examination '" + examination.getTitle() +
                    "' submitted successfully.");
            System.out.println("Result for '" + examination.getTitle() +
                    "' attempt: " + correct + "/" + questions.size() + " correct.");
        }
    }

    public static void main(String[] args) {
        Student student = new Student("Student");

        Examination exam = new Examination("Math Quiz");
        exam.addQuestion(new MultipleChoiceQuestion(
                "2 + 2 = ?", "A"));
        exam.addQuestion(new MultipleChoiceQuestion(
                "Capital of France?", "B"));

        Attempt attempt = exam.startAttempt(student);

        System.out.println("Examination '" + exam.getTitle() +
                "' started by Student.");

        attempt.answerQuestion(1, "A");
        attempt.answerQuestion(2, "C");

        attempt.submit();

        // This demonstrates that submitted answers cannot be changed.
        attempt.answerQuestion(1, "B");
    }
}
