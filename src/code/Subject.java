package code;
import java.util.List;
import java.sql.*;

public class Subject {

    private String instructor, id, name;
    private List<Note> notes;
    private List<Assignment> assignments;
    // Subject
    public Subject(String id, String name, String instructors) {
        this.id = id;
        setName(name);
        setInstructor(instructors);
    }
    public String getId() {
        return id;
    }
    // name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name != null)
            this.name = name;
    }
    // instructor
    public String getInstructor()
    {
        return instructor;
    }
    public void setInstructor(String instructor)
    {
        if (instructor != null)
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