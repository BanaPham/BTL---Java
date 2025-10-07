package code;
import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String subject_id;
    private String name;
    private String instructor;
    private List<Note> notes = new ArrayList<>();
    private List<Assignment> assignments = new ArrayList<>();
    public String get_subject_id()
        return subject_id;
    public void set_subject_id(String subject_id)
        this.subject_id =subject_id;
    public String get_name_subject()
        return name;
    public void set_name_subject(String name)
        this.name =name;
    public String get_instructor()
        return instructor;
    public void set_instructor(String instructor)
        this.instructor =instructor;
    public List<Note> get_notes()
        return notes;
    public void set_notes(List<Note> notes)
        this.notes =notes;
    public List<Assignment> get_assignments()
        return assignments;
    public void set_assignments(List<Assignment> assignments)
        this.assignments =assignments;
    public void add_note(Note note) {
        if (!this.notes.contains(note))
            notes.add(note);
    }
    public void remove_note(Note note) {
        if (this.notes.contains(note))
            notes.remove(note);
    }
    public void add_assignment(Assignment assignment) {
        if (!this.assignments.contains(assignment))
            assignments.add(assignment);
    }
    public void remove_assignment(Assignment assignment) {
        if (this.assignments.contains(assignment))
            assignments.remove(assignment);
    }
}
