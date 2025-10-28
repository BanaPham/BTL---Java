package code;
import java.util.ArrayList;
import java.util.List;
public class Subject extends Object
{
    private String instructor;
    private List<Note> notes;
    private List<Assignment> assignments;
    // Subject

    public Subject(String id, String name, String subject_id, String name1, String instructor, List<Note> notes, List<Assignment> assignments) {
        super(id, name);
        this.instructor = instructor;
        this.notes = notes;
        this.assignments = assignments;
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
        if (this.notes != null)
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
        if (assignments != null)
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
