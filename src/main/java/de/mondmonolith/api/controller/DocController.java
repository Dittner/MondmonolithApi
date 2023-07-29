package de.mondmonolith.api.controller;

import de.mondmonolith.api.controller.dto.DocDto;
import de.mondmonolith.api.controller.dto.Response;
import de.mondmonolith.api.model.Dir;
import de.mondmonolith.api.model.Doc;
import de.mondmonolith.api.model.User;
import de.mondmonolith.api.repository.DirRepo;
import de.mondmonolith.api.repository.DocRepo;
import de.mondmonolith.api.repository.PageRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class DocController {
    @Autowired
    DirRepo dirRepo;

    @Autowired
    DocRepo docRepo;

    @Autowired
    PageRepo pageRepo;

    @GetMapping("dirs/{dirId}/docs")
    public Response getDirDocs(@PathVariable("dirId") Long dirId, @AuthenticationPrincipal User user) {
        try {
            Dir dir = dirRepo.findById(dirId).orElse(null);

            if (dir == null || dir.getUserId() != user.getId()) {
                return new Response("Dir not found", HttpStatus.BAD_REQUEST);
            }

            List<DocDto> res = new ArrayList<>(docRepo.findAllByDirId(dirId)
                    .stream()
                    .filter(doc -> doc.getUserId() == user.getId())
                    .map(doc -> new DocDto(doc.getId(), dirId, doc.getTitle()))
                    .toList());

            if (res.isEmpty()) {
                return new Response(HttpStatus.NO_CONTENT);
            }

            return new Response(res, HttpStatus.OK);
        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("dirs/{dirId}/docs/create")
    public Response createDoc(@Valid @RequestBody CreateDocRequest request,
                              @PathVariable("dirId") Long dirId,
                              @AuthenticationPrincipal User user) {
        try {
            if (request.title.equals("")) {
                return new Response("The doc's title must not be empty", HttpStatus.BAD_REQUEST);
            }

            Dir dir = dirRepo.findById(dirId).orElse(null);

            if (dir == null || dir.getUserId() != user.getId()) {
                return new Response("Dir not found", HttpStatus.BAD_REQUEST);
            }

            Doc doc = docRepo.save(new Doc(user.getId(), dirId, request.title));
            return new Response(new DocDto(doc.getId(), doc.getDirId(), doc.getTitle()), HttpStatus.CREATED);
        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("dirs/{dirId}/docs/update")
    public Response updateDoc(@PathVariable("dirId") Long dirId,
                              @Valid @RequestBody DocDto request,
                              @AuthenticationPrincipal User user) {
        try {
            if (request.title.equals("")) {
                return new Response("The doc's title must not be empty", HttpStatus.BAD_REQUEST);
            }

            Doc doc = docRepo.findById(request.id).orElse(null);
            if (doc == null || doc.getUserId() != user.getId()) {
                return new Response("Doc not found", HttpStatus.NOT_FOUND);
            }

            if (dirId != request.dirId) {
                Dir dir = dirRepo.findById(request.dirId).orElse(null);
                if (dir == null || dir.getUserId() != user.getId()) {
                    return new Response("Dir not found", HttpStatus.NOT_FOUND);
                }
            }

            doc.setDirId(request.dirId);
            doc.setTitle(request.title);
            docRepo.save(doc);
            return new Response(HttpStatus.OK);

        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @DeleteMapping("dirs/{dirId}/docs/{docId}")
    public Response deleteDoc(@PathVariable("docId") Long docId,
                              @AuthenticationPrincipal User user) {
        try {
            Doc doc = docRepo.findById(docId).orElse(null);
            if (doc == null || doc.getUserId() != user.getId()) {
                return new Response("Doc not found", HttpStatus.NOT_FOUND);
            }

            pageRepo.deleteAllByDocId(docId);
            docRepo.deleteById(docId);

            return new Response(HttpStatus.OK);

        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

class CreateDocRequest {
    public String title;
}