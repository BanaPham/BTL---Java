package code;
import java.util.ArrayList;
import java.util.List;
public class Subject
{
    private String subject_id;
    private String name;
    private String instructor;
    private List<Note> notes;
    private List<Assignment> assignments;
    // Subject
    public Subject()
    {
        this.subject_id = "";
        this.name = "";
        this.instructor = "";
        this.notes = new ArrayList<>();
        this.assignments = new ArrayList<>();
    }
    public Subject(String subject_id, String name, String instructor)
    {
        this.subject_id = subject_id;
        this.name = name;
        this.instructor = instructor;
        this.notes = new ArrayList<>();
        this.assignments = new ArrayList<>();
    }
    // subject_id
    public String getSubjectId()
    {
        return subject_id;
    }
    public void setSubjectId(String subject_id)
    {
        this.subject_id = subject_id;
    }
    // name
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    // instructor
    public String getInstructor()
    {
        return instructor;
    }
    public void setInstructor(String instructor)
    {
        this.instructor = instructor;
    }
    // Note 
    public List<Note> getNotes()
    {
        return notes;
    }
    public void setNotes(List<Note> notes)
    {
        this.notes = notes;
    }
    public void addNote(Note note)
    {
        if(!this.notes.contains(note))
            this.notes.add(note);
    }
    public void removeNote(Note note)
    {
        if(this.notes.contains(note))
            this.notes.remove(note);
    }
    // Assignment
    public List<Assignment> getAssignments()
    {
        return assignments;
    }
    public void setAssignments(List<Assignment> assignments)
    {
        this.assignments = assignments;
    }
    public void addAssignment(Assignment assignment)
    {
        if(!this.assignments.contains(assignment))
            this.assignments.add(assignment);
    }
    public void removeAssignment(Assignment assignment)
    {
        if(this.assignments.contains(assignment))
            this.assignments.remove(assignment);
    }
}
