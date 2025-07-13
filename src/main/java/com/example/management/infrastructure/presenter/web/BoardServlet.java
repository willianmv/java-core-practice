package com.example.management.infrastructure.presenter.web;

import com.example.management.core.dto.output.CompleteBoardOutput;
import com.example.management.core.usecase.board.CompleteBoardUseCase;
import com.example.management.infrastructure.config.AppContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/boards")
public class BoardServlet extends HttpServlet {

    private final CompleteBoardUseCase completeBoardUseCase = AppContext.getInstance()
            .get(CompleteBoardUseCase.class);

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String idParam = req.getParameter("id");

        if (idParam == null || idParam.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "ID do board é obrigatório");
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try{
            long boardId = Long.parseLong(idParam);
            CompleteBoardOutput boardOutput = completeBoardUseCase.execute(boardId);

            if (boardOutput == null){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Board não encontrado");
                return;
            }

            String json = mapper.writeValueAsString(boardOutput);
            resp.getWriter().write(json);

        } catch (NumberFormatException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }

    private void writeError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        String errorJson = mapper.writeValueAsString(new ErrorResponse(message));
        resp.getWriter().write(errorJson);
    }

    private static class ErrorResponse {
        public final String erro;

        public ErrorResponse(String erro) {
            this.erro = erro;
        }
    }

}
