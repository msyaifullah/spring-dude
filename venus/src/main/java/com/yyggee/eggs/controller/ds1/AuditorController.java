package com.yyggee.eggs.controller.ds1;

import com.yyggee.eggs.model.ds1.Auditor;
import com.yyggee.eggs.service.ds1.AuditorService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/audits"})
@Slf4j
public class AuditorController {

  @Autowired private AuditorService auditorService;

  @GetMapping
  public List<Auditor> findAll() {
    return auditorService.findAll();
  }

  @PostMapping
  public ResponseEntity<Auditor> create(@Valid @RequestBody Auditor audit) {
    return ResponseEntity.ok(auditorService.createOrUpdate(audit));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Auditor> findById(@PathVariable long id) {

    Optional<Auditor> Audit = auditorService.findById(id);

    if (!Audit.isPresent()) {
      log.error("Audit with id " + id + " does not exist.");
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(Audit.get());
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<Auditor> update(@PathVariable long id, @Valid @RequestBody Auditor audit) {

    Optional<Auditor> p = auditorService.findById(id);

    if (!p.isPresent()) {
      log.error("Audit with id " + id + " does not exist.");
      return ResponseEntity.notFound().build();
    }

    p.get().setUsername(audit.getUsername());
    p.get().setMethod(audit.getMethod());
    p.get().setUrl(audit.getUrl());
    p.get().setStatus(audit.getStatus());

    return ResponseEntity.ok(auditorService.createOrUpdate(p.get()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity deleteById(@PathVariable long id) {
    Optional<Auditor> p = auditorService.findById(id);

    if (!p.isPresent()) {
      log.error("Audit with id " + id + " does not exist.");
      return ResponseEntity.notFound().build();
    }

    auditorService.deleteById(id);
    return ResponseEntity.ok().build();
  }
}
