import java.util.*;
 
enum HackathonState { OPEN, JUDGING, PUBLISHED }
 
class Student {
    private final String name;
 
    Student(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Student name is blank");
        this.name = name;
    }
 
    String getName() { return name; }
}
 
class Score {
    private final int idea, execution, presentation;
 
    Score(int idea, int execution, int presentation) {
        if (!valid(idea) || !valid(execution) || !valid(presentation)) {
            throw new IllegalArgumentException("Ratings must be between 0 and 10");
        }
        this.idea = idea;
        this.execution = execution;
        this.presentation = presentation;
    }
 
    private static boolean valid(int r) { return r >= 0 && r <= 10; }
 
    int getIdea() { return idea; }
    int getExecution() { return execution; }
    int getPresentation() { return presentation; }
}
 
interface ScoringRule {
    String getTrackName();
    double compute(Score score);
}
 
class InnovationTrack implements ScoringRule {
    public String getTrackName() { return "Innovation"; }
 
    public double compute(Score s) {
        return s.getIdea() * 0.5 + s.getExecution() * 0.3 + s.getPresentation() * 0.2;
    }
}
 
class OpenTrack implements ScoringRule {
    public String getTrackName() { return "Open"; }
 
    public double compute(Score s) {
        return (s.getIdea() + s.getExecution() + s.getPresentation()) / 3.0;
    }
}
 
class Team {
    private final String name;
    private final List<Student> members;
    private final ScoringRule track;
    private Project project;                       // 0..1
 
    Team(String name, List<Student> members, ScoringRule track) {
        this.name = name;
        this.members = List.copyOf(members);
        this.track = track;
    }
 
    String getName() { return name; }
    List<Student> getMembers() { return members; }
    ScoringRule getTrack() { return track; }
    Project getProject() { return project; }
 
    void setProject(Project p) {
        if (project != null) throw new IllegalStateException("Team " + name + " has already submitted a project");
        project = p;
    }
}
 
class Project {
    private final String title;
    private final Team team;
    private final Map<Judge, Score> scores = new LinkedHashMap<>();
 
    Project(String title, Team team) {
        this.title = title;
        this.team = team;
    }
 
    String getTitle() { return title; }
    Team getTeam() { return team; }
 
    void putScore(Judge judge, Score score) { scores.put(judge, score); }
 
    // Average over judges of the track-specific score
    double getFinalScore() {
        if (scores.isEmpty()) return 0;
        ScoringRule rule = team.getTrack();
        double total = 0;
        for (Score s : scores.values()) total += rule.compute(s);
        return total / scores.size();
    }
}
 
class Judge {
    private final String name;
 
    Judge(String name) { this.name = name; }
 
    String getName() { return name; }
 
    double score(Hackathon h, Project p, int idea, int execution, int presentation) {
        return h.recordScore(this, p, new Score(idea, execution, presentation));
    }
}
 
class Hackathon {
    private static final String LOCKED = "Results have already been published";
 
    private final String name;
    private HackathonState state = HackathonState.OPEN;
    private final List<Team> teams = new ArrayList<>();
    private final Set<Student> registeredStudents = new HashSet<>();
    private final Set<String> teamNames = new HashSet<>();
 
    Hackathon(String name) { this.name = name; }
 
    HackathonState getState() { return state; }
 
    Team registerTeam(String teamName, List<Student> members, ScoringRule track) {
        if (state != HackathonState.OPEN) throw new IllegalStateException("Registration is closed");
        if (members.size() < 2 || members.size() > 4) {
            throw new IllegalArgumentException("A team must have 2 to 4 members");
        }
        if (new HashSet<>(members).size() != members.size()) {
            throw new IllegalArgumentException("A student cannot be listed twice");
        }
        for (Student s : members) {
            if (registeredStudents.contains(s)) {
                throw new IllegalArgumentException(s.getName() + " is already in another team");
            }
        }
        if (!teamNames.add(teamName)) throw new IllegalArgumentException("Team name '" + teamName + "' is taken");
 
        Team team = new Team(teamName, members, track);   // all checks passed -> mutate
        teams.add(team);
        registeredStudents.addAll(members);
        return team;
    }
 
    Project submitProject(Team team, String title) {
        if (state == HackathonState.PUBLISHED) throw new IllegalStateException(LOCKED);
        if (!teams.contains(team)) throw new IllegalArgumentException("Team is not registered in this hackathon");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Project title is blank");
        Project p = new Project(title, team);
        team.setProject(p);                                // enforces one project per team
        return p;
    }
 
    // Adds or replaces a judge's score; returns the project's current final score
    double recordScore(Judge judge, Project project, Score score) {
        if (state == HackathonState.PUBLISHED) throw new IllegalStateException(LOCKED);
        if (!teams.contains(project.getTeam())) {
            throw new IllegalArgumentException("Project does not belong to this hackathon");
        }
        if (state == HackathonState.OPEN) state = HackathonState.JUDGING;
        project.putScore(judge, score);
        return project.getFinalScore();
    }
 
    void publishResults() {
        if (state == HackathonState.PUBLISHED) throw new IllegalStateException(LOCKED);
        state = HackathonState.PUBLISHED;
    }
}
 
public class Main {
    static Team register(Hackathon h, String name, ScoringRule track, Student... members) {
        try {
            Team t = h.registerTeam(name, Arrays.asList(members), track);
            System.out.println("Team " + name + " registered (" + members.length + " members, "
                    + track.getTrackName() + " track).");
            return t;
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Registration failed: " + e.getMessage() + ".");
            return null;
        }
    }
 
    public static void main(String[] args) {
        Hackathon hackathon = new Hackathon("Code Sprint");
        Judge judge = new Judge("Dr. Rao");
 
        Student asha = new Student("Asha"), ravi = new Student("Ravi"),
                neha = new Student("Neha"), kiran = new Student("Kiran");
 
        Team byteBusters = register(hackathon, "ByteBusters", new InnovationTrack(), asha, ravi, neha);
        register(hackathon, "SoloCoder", new OpenTrack(), kiran);
 
        Project project = hackathon.submitProject(byteBusters, "SmartAttend");
        System.out.println("Project '" + project.getTitle() + "' submitted by " + byteBusters.getName() + ".");
 
        double finalScore = judge.score(hackathon, project, 8, 7, 9);
        System.out.println("Score recorded for '" + project.getTitle() + "'. Final score: "
                + String.format(Locale.US, "%.2f", finalScore) + ".");
 
        hackathon.publishResults();
        System.out.println("Results published.");
 
        try {
            judge.score(hackathon, project, 10, 7, 9);
        } catch (IllegalStateException e) {
            System.out.println("Rescore rejected: " + e.getMessage() + ".");
        }
    }
}
