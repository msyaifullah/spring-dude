package com.yyggee.eggs.model.ds1;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "auditor")
public class Auditor implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NonNull private String username;
  @NonNull private String method;
  @NonNull private String status;
  @NonNull private String url;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Auditor auditor = (Auditor) o;
    return Objects.equals(id, auditor.id)
        && Objects.equals(username, auditor.username)
        && Objects.equals(method, auditor.method)
        && Objects.equals(status, auditor.status)
        && Objects.equals(url, auditor.url);
  }
}
