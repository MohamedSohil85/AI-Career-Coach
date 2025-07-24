package com.aicareercoach.service;

import com.aicareercoach.model.JobCandidate;
import com.aicareercoach.model.Skills;
import com.aicareercoach.persistence.*;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class JobCandidateService {

private final JobCandidateRepository jobCandidateRepository;
private final WorkExperienceRepository workexperienceRepository;
private final CertificationRepository CertificationRepository;
private final SkillsRepository skillsRepository;
private final EducationRepository educationRepository;
private final LanguageRepository languageRepository;
private final RestTemplate restTemplate = new RestTemplate();
private final String OLLAMA_URL = "http://localhost:11434";
private final ChatLanguageModel languageModel;
private final AiServices aiServices;



    public JobCandidateService(JobCandidateRepository jobCandidateRepository, WorkExperienceRepository workexperienceRepository, com.aicareercoach.persistence.CertificationRepository certificationRepository, SkillsRepository skillsRepository, EducationRepository educationRepository, LanguageRepository languageRepository) {
        this.jobCandidateRepository = jobCandidateRepository;
        this.workexperienceRepository = workexperienceRepository;
        this.CertificationRepository = certificationRepository;
        this.skillsRepository = skillsRepository;
        this.educationRepository = educationRepository;
        this.languageRepository = languageRepository;
        this.languageModel = OllamaChatModel
                .builder()
                .baseUrl(OLLAMA_URL)
                .modelName("llama3.2")
                .temperature(0.7)
                .timeout(Duration.ofMinutes(5)).maxRetries(3)
                .build();;
        this.aiServices = AiServices.builder(AiServices.class)
                .chatLanguageModel(languageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10));    }
    public JobCandidate saveNewCandidate(JobCandidate jobCandidate) {

        var skills = jobCandidate.getSkills();
        var languages = jobCandidate.getLanguages();
        var workExperiences = jobCandidate.getWorkExperience();
        var certifications = jobCandidate.getCertifications();
        var educations=jobCandidate.getLevelOfEducation();

        jobCandidate.setSkills(skills);
        jobCandidate.setLanguages(languages);
        jobCandidate.setWorkExperience(workExperiences);
        jobCandidate.setCertifications(certifications);
        jobCandidate.setLevelOfEducation(educations);

        skillsRepository.saveAll(skills);
        languageRepository.saveAll(languages);
        workexperienceRepository.saveAll(workExperiences);
        CertificationRepository.saveAll(certifications);
        educationRepository.saveAll(educations);


        return jobCandidateRepository.save(jobCandidate);
    }

    public String generateSkillsQuiz(String skill,String difficulty) throws IOException {



        List<String> readFileContent = Files.readAllLines(toPath("quiz.txt"));
        List<String> content = new ArrayList<>(readFileContent);
        String quiz = String.join("\n", content);
        // Build the full prompt
        String fullPrompt = String.join("\n\n",
                "You are technical Assistant, who create a Quiz Questions according to the following skill"+skill+" with difficulty"+ difficulty+"follow the instructions in the file"+
                quiz+"and Do not invent anything else."
        );
        String response=languageModel.generate(fullPrompt);
        System.out.println("Prompt: \n" + fullPrompt);
        System.out.println("Response from Ollama: " + response);

        return response != null ? response.trim() : "No answer from AI.";
    }

    public String generateCV(Long id) throws IOException {

       Optional<JobCandidate>jobCandidate = jobCandidateRepository.findById(id);
       JobCandidate jobCandidate_ = jobCandidate.get();



        List<String> readFileContent = Files.readAllLines(toPath("ATS_Friendly_Resume_Guide.txt"));
        List<String> content = new ArrayList<>(readFileContent);

        String ats = String.join("\n", content);

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
                "You are an AI career coach and CV writer" +"Generate for"+jobCandidate_+" a professional CV according to criteria :"+ Arrays.toString(ats_tips)+" and follow  structure the resume exactly based on the following template/content:"+ats+"write the CV according to the skills of candidate"+jobCandidate_.getSoftSkills()+" work experiences , educations"+jobCandidate_.getWorkExperience()+" "+jobCandidate_.getLevelOfEducation()+
                   " Language"+jobCandidate_.getLanguages()+"and Certifications"+jobCandidate_.getCertifications()+     " please Do not invent anything. If you don't know the answer, say so."

                );
 /* Map<String, Object> req = new HashMap<>();
        req.put("model", "llama3.2");
        req.put("prompt", fullPrompt);
        req.put("stream", false);


        Map<?, ?> res = restTemplate.postForObject("http://localhost:11434/api/generate", req, Map.class);
        Object response = res != null ? res.get("response") : null;*/
        String response =languageModel.generate(fullPrompt);
        return response != null ? response.trim(): "No answer from AI.";
    }


    private static Path toPath(String file) {
        try {
            URL fileUrl = AICareerCoachService.class.getClassLoader().getResource(file);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
