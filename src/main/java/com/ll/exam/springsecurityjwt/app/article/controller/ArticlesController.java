package com.ll.exam.springsecurityjwt.app.article.controller;

import com.ll.exam.springsecurityjwt.app.article.dto.request.ArticleModifyDto;
import com.ll.exam.springsecurityjwt.app.article.entity.Article;
import com.ll.exam.springsecurityjwt.app.article.service.ArticleService;
import com.ll.exam.springsecurityjwt.app.base.dto.RsData;
import com.ll.exam.springsecurityjwt.app.member.security.entity.MemberContext;
import com.ll.exam.springsecurityjwt.util.Util;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticlesController {
    private final ArticleService articleService;

    @GetMapping("")
    public ResponseEntity<RsData> list() {
        List<Article> articles = articleService.findAll();

        return Util.spring.responseEntityOf(
                RsData.successOf(
                        Util.mapOf(
                                "articles", articles
                        )
                )
        );
    }
    @GetMapping("{id}")
    public ResponseEntity<RsData> detail(@PathVariable Long id) {
        Article article = articleService.findById(id).orElse(null);

        if (article == null) {
            return Util.spring.responseEntityOf(
                    RsData.of(
                            "F-1",
                            "해당 게시물은 존재하지 않습니다."
                    )
            );
        }

        return Util.spring.responseEntityOf(
                RsData.successOf(
                        Util.mapOf(
                                "article", article
                        )
                )
        );
    }
    @DeleteMapping("{id}")
    public ResponseEntity<RsData> delete(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal MemberContext memberContext
    ) {
        Article article = articleService.findById(id).orElse(null);

        if (article == null) {
            return Util.spring.responseEntityOf(
                    RsData.of(
                            "F-1",
                            "해당 게시물은 존재하지 않습니다."
                    )
            );
        }

        if (articleService.actorCanDelete(memberContext, article) == false) {
            return Util.spring.responseEntityOf(
                    RsData.of(
                            "F-2",
                            "삭제 권한이 없습니다."
                    )
            );
        }

        articleService.delete(article);

        return Util.spring.responseEntityOf(
                RsData.of(
                        "S-1",
                        "해당 게시물이 삭제되었습니다."
                )
        );
    }
    @PatchMapping("{id}")
    public ResponseEntity<RsData> modify(@PathVariable Long id, @AuthenticationPrincipal MemberContext memberContext, @Valid @RequestBody ArticleModifyDto articleModifyDto) {
        Article article = articleService.findById(id).orElse(null);

        if (article == null) {
            return Util.spring.responseEntityOf(
                    RsData.of(
                            "F-1",
                            "해당 게시물은 존재하지 않습니다."
                    )
            );
        }

        if (articleService.actorCanModify(memberContext, article) == false) {
            return Util.spring.responseEntityOf(
                    RsData.of(
                            "F-2",
                            "수정 권한이 없습니다."
                    )
            );
        }

        articleService.modify(article, articleModifyDto);

        return Util.spring.responseEntityOf(
                RsData.of(
                        "S-1",
                        "해당 게시물이 수정되었습니다."
                )
        );
    }
}
