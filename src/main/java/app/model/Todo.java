package app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document
public class Todo implements Serializable {
  @Id
  private String id;

  //Transient annotation that makes this field not persist in MongoDB
  @Transient
  private boolean selected;

  private LocalDateTime createdAt;
  private String title;
  private String body;
  private String author;

  private boolean closed = false;

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }
}
