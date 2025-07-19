package com.aicareercoach.service;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import static dev.langchain4j.model.ollama.OllamaChatModel.builder;

@Service
public class AICareerCoachService {


    private final String OLLAMA_URL = "http://localhost:11434";
    private final ChatLanguageModel languageModel;
    private final AiServices aiServices;

    public AICareerCoachService() {
        this.languageModel= OllamaChatModel.builder()
                .baseUrl(OLLAMA_URL)
                .modelName("llama3.2")
                .temperature(0.5)
                .timeout(Duration.ofMinutes(5)).maxRetries(3)
                .build();
        this.aiServices=AiServices.builder(AiServices.class)
                .chatLanguageModel(languageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10));



    }

    public String careerCoach(MultipartFile file) {
        if (file.isEmpty()) {
            return "No file selected";
        }

        if (!Objects.equals(file.getContentType(), "application/pdf")) {
            return "Only PDF files are allowed";
        }

        try (InputStream inputStream = file.getInputStream()) {
            // read from the pdf file direct
            String extractedText = askAICareerCoach(inputStream);
            // retrieve the Answer
            return "the Answer from AI :\n" + extractedText;


        } catch (IOException e) {
            return "Failed to process file";
        }
    }
    private String askAICareerCoach(InputStream file) throws IOException {

        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();

        List<String> readFileContent = Files.readAllLines(Paths.get("C:\\Users\\momi_\\IdeaProjects\\OllamaProject\\src\\main\\resources\\ATS_Friendly_Resume_Guide.txt"));
        List<String> content = new ArrayList<>(readFileContent);

        String ats = String.join("\n", content);
        String text = stripper.getText(document);
        String[] ats_tips ={
                "Use standard fonts (Arial, Calibri, Times New Roman).",
                "Avoid tables, text boxes, columns, and graphics.",

                "Font size: 12–14 pt for headings, 10–12 pt for body text.",
                "Save as .docx or text-based .pdf (not image-based).",
                "Use simple headings like 'Work Experience', 'Education', etc.",
                "Left-align text and use simple bullet points.",

                "Include keywords from the job description.",
                "Mention job titles, tools, and relevant skills.",
                "Use variations of key terms (e.g., 'Project Management' and 'PM').",
                "Include: Contact Info, Summary, Work Experience, Education, Skills, Certifications.",
                "List clear job titles with company name, location, and dates (Month/Year).",
                "Avoid headers/footers.",
                "Avoid images, icons, or unusual fonts/colors,multi-column layouts",
                "Always write acronyms in full at least once (e.g., 'Search Engine Optimization (SEO)')."};

        String fullPrompt = String.join("\n\n",
                "You are a AI Career Assistant." +"Check if the PDF file for ATS-friendly according to criteria :"+ Arrays.toString(ats_tips)+"Provide an evaluation result, and then help reformat or structure the resume exactly based on the following template/content:"+ats+
                        " please Do not invent anything. If you don't know the answer, say so.",
                text,
                "Answer:");
 /* Map<String, Object> req = new HashMap<>();
        req.put("model", "llama3.2");
        req.put("prompt", fullPrompt);
        req.put("stream", false);


        Map<?, ?> res = restTemplate.postForObject("http://localhost:11434/api/generate", req, Map.class);
        Object response = res != null ? res.get("response") : null;*/
        String response =languageModel.generate(fullPrompt);
        return response != null ? response.trim(): "No answer from AI.";
    }

    public String generateCoverLetter(MultipartFile resume, String role, String jobDescription, String companyName, String place) throws IOException {


        PDDocument document = PDDocument.load(resume.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        // Text in ein Document-Objekt konvertieren
        //  Document doc = new Document(text); //

        // 1. EmbeddingStore + EmbeddingModel
        EmbeddingModel embeddingModel=  new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

// 2. Ingestor mit Text-Dokument
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(Document.from(text));// Nur EIN Document

        List<String> readFileContent = Files.readAllLines(Paths.get("C:\\Users\\momi_\\IdeaProjects\\OllamaProject\\src\\main\\resources\\CLT.txt"));
        List<String> content = new ArrayList<>(readFileContent);

        String coverTemp = String.join("\n", content);

        // Chat-Setup
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        String systemPrompt = String.format(
                "You are an AI Career Coach. Generate a professional cover letter for the role of: %s.\n" +
                        "Job Description: %s\n" +
                        "Company Name: %s\n" +
                        "Location: %s\n\n" +
                        "Please follow the structure and tone of the following cover letter template:\n%s\n\n" +
                        "Here is the candidate's resume:\n%s\n\n" +
                        "Do not make up or invent any information. Use only what's provided.",
                role, jobDescription, companyName, place, coverTemp, text
        );

        chatMemory.add(new SystemMessage(systemPrompt));

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.75)//
                .build();

// Create a RetrievalAugmentor using your ContentRetriever

// Create a RetrievalAugmentor using your ContentRetriever
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .build();

        ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
                .chatLanguageModel(languageModel)
                .chatMemory(chatMemory)
                .retrievalAugmentor(retrievalAugmentor) // Use retrievalAugmentor instead of retriever
                .build();




        String userPrompt = String.format(
                "Please write a cover letter for the '%s' role at '%s', based on the job description and my resume.",
                role, companyName
        );
        return chain.execute(userPrompt);
    }

    public String generateAIInterviewQuestions(MultipartFile resume,String jobDescription,String role) throws IOException {

        PDDocument doc = PDDocument.load(resume.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(doc);
        doc.close();

        // Text in ein Document-Objekt konvertieren
        //  Document doc = new Document(text); //

        // 1. EmbeddingStore + EmbeddingModel
        EmbeddingModel embeddingModel=  new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

// 2. Ingestor with Text-Dokument
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(Document.from(text));

        List<String> readFileContent_1 = Files.readAllLines(Paths.get("C:\\Users\\momi_\\IdeaProjects\\OllamaProject\\src\\main\\resources\\instruction.txt"));
        List<String> content = new ArrayList<>(readFileContent_1);

        String instruction = String.join("\n", content);

        List<String> readFileContent_2 = Files.readAllLines(Paths.get("C:\\Users\\momi_\\IdeaProjects\\OllamaProject\\src\\main\\resources\\response.txt"));
        List<String> content_2 = new ArrayList<>(readFileContent_2);

        String response = String.join("\n", content_2);

        //
        // Chat-Setup
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        String systemPrompt = String.format(
                """
                        You are an AI Career Coach. follow the Instructions in the provided file: %s.
                        Job Description: %s
                        Role: %s
                        Here is the candidate's resume: %s
                        and By Generation the Question ,Follow the Template in the provided file: %s
                        Do not make up or invent any information. Use only what's provided.""",
                instruction,jobDescription,role ,text,response
        );

        chatMemory.add(new SystemMessage(systemPrompt));

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.75)//
                .build();


// Create a RetrievalAugmentor using your ContentRetriever
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .build();

        ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
                .chatLanguageModel(languageModel)
                .chatMemory(chatMemory)
                .retrievalAugmentor(retrievalAugmentor) // Use retrievalAugmentor instead of retriever
                .build();



        String userPrompt = String.format(
                """
                        Please analyze the job description for the role of %s and the candidate's resume.
                        Based on the instructions in the file: %s and using the question generation template in the file: %s,
                        generate interview questions tailored specifically to this candidate and this role.
                        Ensure that all questions are relevant to the job and grounded in the candidate's actual experience and skills.
                        Do not include any information not present in the provided documents.
                        Please present the questions in the format specified in the template.
                        """,
                role, instruction, response
        );

        return chain.execute(userPrompt);

    }



    public String gapSkills(MultipartFile resume, String targetRole) throws IOException {

        PDDocument doc = PDDocument.load(resume.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(doc);
        doc.close();

        // Text in ein Document-Objekt konvertieren
        //  Document doc = new Document(text); //

        // 1. EmbeddingStore + EmbeddingModel
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

// 2. Ingestor with Text-Dokument
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 0))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(Document.from(text));

        List<String> readFileContent = Files.readAllLines(toPath("gap_skills.txt"));
        List<String> content = new ArrayList<>(readFileContent);

        String instruction = String.join("\n", content);

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        String systemPrompt = String.format(
                """
                        You are an AI Career Coach. follow the Instructions in the provided file: %s.
                        Target Role: %s
                        Here is the candidate's resume: %s
                        Do not make up or invent any information. Use only what's provided.""",
                instruction, targetRole, text
        );

        //
        chatMemory.add(new SystemMessage(systemPrompt));

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.75)//
                .build();


// Create a RetrievalAugmentor using your ContentRetriever
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .build();

        ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
                .chatLanguageModel(languageModel)
                .chatMemory(chatMemory)
                .retrievalAugmentor(retrievalAugmentor) // Use retrievalAugmentor instead of retriever
                .build();

        String userPrompt = String.format(
                """
                        You are an AI career advisor specialized in technical roles.
                        Please analyze the resume of %s and compare it to the target role: %s.
                        Identify the skill gaps between the candidate’s current skills and the required skills for the desired role.
                        Based on this analysis .Only use information present in the resume and role description to determine skill gaps."""
                , text, targetRole);

        return chain.execute(userPrompt);


    }



}
