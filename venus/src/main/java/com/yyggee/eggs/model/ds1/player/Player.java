package com.yyggee.eggs.model.ds1.player;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@AllArgsConstructor
@Entity
@Table(name = "player")
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(
      name = "id",
      columnDefinition = "VARCHAR(255)",
      insertable = false,
      updatable = false,
      nullable = false)
  private UUID id;

  private String name;
  private String username;
  private String email;
  private String phone;
  private String website;

  @Embedded private Address address;
  @Embedded private Company company;

  public Player() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Player player = (Player) o;
    return Objects.equals(id, player.id)
        && Objects.equals(name, player.name)
        && Objects.equals(username, player.username)
        && Objects.equals(email, player.email)
        && Objects.equals(phone, player.phone)
        && Objects.equals(website, player.website)
        && Objects.equals(address, player.address)
        && Objects.equals(company, player.company);
  }
}
