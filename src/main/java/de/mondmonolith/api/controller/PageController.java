package de.mondmonolith.api.controller;

import de.mondmonolith.api.controller.dto.PageDto;
import de.mondmonolith.api.controller.dto.Response;
import de.mondmonolith.api.model.Doc;
import de.mondmonolith.api.model.Page;
import de.mondmonolith.api.model.User;
import de.mondmonolith.api.repository.DocRepo;
import de.mondmonolith.api.repository.PageRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/dirs/{dirId}/")
public class PageController {
    @Autowired
    DocRepo docRepo;

    @Autowired
    PageRepo pageRepo;

    @GetMapping("docs/{docId}/pages")
    public Response getDocPages(@PathVariable("docId") Long docId, @AuthenticationPrincipal User user) {
        try {
            Doc doc = docRepo.findById(docId).orElse(null);

            if (doc == null || doc.getUserId() != user.getId()) {
                return new Response("Doc not found", HttpStatus.BAD_REQUEST);
            }

            List<PageDto> res = new ArrayList<>(pageRepo.findAllByDocId(docId)
                    .stream()
                    .filter(p -> p.getUserId() == user.getId())
                    .map(p -> new PageDto(p.getId(), docId, p.getTitle(), p.getBlocks()))
                    .toList());

            if (res.isEmpty()) {
                return new Response(HttpStatus.NO_CONTENT);
            }

            return new Response(res, HttpStatus.OK);
        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("docs/{docId}/pages/create")
    public Response createPage(@Valid @RequestBody CreatePageRequest request,
                               @PathVariable("docId") Long docId,
                               @AuthenticationPrincipal User user) {
        try {
            if (request.title.equals("")) {
                return new Response("The page's title must not be empty", HttpStatus.BAD_REQUEST);
            }

            Doc doc = docRepo.findById(docId).orElse(null);

            if (doc == null || doc.getUserId() != user.getId()) {
                return new Response("Doc not found", HttpStatus.BAD_REQUEST);
            }

            Page page = pageRepo.save(new Page(user.getId(), docId, request.title, request.blocks));
            return new Response(new PageDto(page.getId(), page.getDocId(), page.getTitle(), page.getBlocks()), HttpStatus.CREATED);
        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("docs/{docId}/pages/update")
    public Response updatePage(@PathVariable("docId") Long docId,
                               @Valid @RequestBody PageDto request,
                               @AuthenticationPrincipal User user) {
        try {
            if (request.title.equals("")) {
                return new Response("The page's title must not be empty", HttpStatus.BAD_REQUEST);
            }

            Page page = pageRepo.findById(request.id).orElse(null);
            if (page == null || page.getUserId() != user.getId()) {
                return new Response("Page not found", HttpStatus.NOT_FOUND);
            }

            if (!Objects.equals(docId, request.docId)) {
                Doc doc = docRepo.findById(request.docId).orElse(null);
                if (doc == null || doc.getUserId() != user.getId()) {
                    return new Response("Doc not found", HttpStatus.NOT_FOUND);
                }
            }

            page.setDocId(request.docId);
            page.setTitle(request.title);
            page.setBlocks(request.blocks);
            pageRepo.save(page);
            return new Response(HttpStatus.OK);

        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("docs/{docId}/pages/{pageId}")
    public Response deletePage(@PathVariable("pageId") Long pageId,
                               @AuthenticationPrincipal User user) {
        try {
            Page page = pageRepo.findById(pageId).orElse(null);
            if (page == null || !Objects.equals(page.getUserId(), user.getId())) {
                return new Response("Page not found", HttpStatus.NOT_FOUND);
            }

            pageRepo.deleteById(pageId);
            return new Response(HttpStatus.OK);

        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

class CreatePageRequest {
    public String title;
    public String[] blocks;
}