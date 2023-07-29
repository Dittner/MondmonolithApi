package de.mondmonolith.api.controller;

import de.mondmonolith.api.controller.dto.DirDto;
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
public class DirController {
    @Autowired
    DirRepo dirRepo;

    @Autowired
    DocRepo docRepo;

    @Autowired
    PageRepo pageRepo;

    @GetMapping("dirs")
    public Response getUserDirs(@AuthenticationPrincipal User user) {
        try {
            List<DirDto> res = new ArrayList<>(dirRepo.findAllByUserId(user.getId())
                    .stream()
                    .map(dir -> new DirDto(dir.getId(), dir.getTitle()))
                    .toList());

            if (res.isEmpty()) {
                return new Response(HttpStatus.NO_CONTENT);
            }

            return new Response(res, HttpStatus.OK);
        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("dirs/create")
    public Response createDir(@Valid @RequestBody CreateDirRequest request, @AuthenticationPrincipal User user) {
        try {
            if (request.title.equals("")) {
                return new Response("The dir's title must not be empty", HttpStatus.BAD_REQUEST);
            }

            Dir dir = dirRepo.save(new Dir(user.getId(), request.title));

            return new Response(new DirDto(dir.getId(), dir.getTitle()), HttpStatus.CREATED);
        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("dirs/update")
    public Response updateDir(@Valid @RequestBody DirDto request,
                              @AuthenticationPrincipal User user) {
        try {
            if (request.title.equals("")) {
                return new Response("The dir's title must not be empty", HttpStatus.BAD_REQUEST);
            }

            Dir dir = dirRepo.findById(request.id).orElse(null);

            if (dir == null || dir.getUserId() != user.getId()) {
                return new Response("Dir not found", HttpStatus.NOT_FOUND);
            }

            dir.setTitle(request.title);
            dirRepo.save(dir);
            return new Response(HttpStatus.OK);

        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @DeleteMapping("dirs/{dirId}")
    public Response deleteDir(@PathVariable("dirId") Long dirId,
                              @AuthenticationPrincipal User user) {
        try {
            Dir dir = dirRepo.findById(dirId).orElse(null);
            if (dir == null || dir.getUserId() != user.getId()) {
                return new Response("Dir not found", HttpStatus.NOT_FOUND);
            }

            docRepo.findAllByDirId(dirId)
                    .stream()
                    .filter(doc -> doc.getUserId() == user.getId())
                    .forEach(doc -> {
                        pageRepo.deleteAllByDocId(doc.getId());
                        docRepo.deleteById(doc.getId());
                    });

            dirRepo.deleteById(dirId);

            return new Response(HttpStatus.OK);

        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

class CreateDirRequest {
    public String title;
}