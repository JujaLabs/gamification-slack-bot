package juja.microservices.gamification.slackbot.model.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author Vadim Dyachenko
 */
@Getter
public class GamificationDTO {

   private int httpStatus;
   private String internalErrorCode;
   private String clientMessage;
   private String developerMessage;
   private String exceptionMessage;
   private String[] detailErrors;
   private String[] ids;

   @JsonCreator
   public GamificationDTO (@JsonProperty("httpStatus") int httpStatus,
                           @JsonProperty("internalErrorCode") String internalErrorCode,
                           @JsonProperty("clientMessage") String clientMessage,
                           @JsonProperty("developerMessage") String developerMessage,
                           @JsonProperty("exceptionMessage") String exceptionMessage,
                           @JsonProperty("detailErrors") String[] detailErrors) {

       this.httpStatus = httpStatus;
       this.internalErrorCode = internalErrorCode;
       this.clientMessage = clientMessage;
       this.developerMessage = developerMessage;
       this.exceptionMessage = exceptionMessage;
       this.detailErrors = detailErrors;
   }

   @JsonCreator
   public GamificationDTO (@JsonProperty String[] ids ) {
       this.ids = ids;
   }
}