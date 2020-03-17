package com.wxsoft.telereciver.entity.responsedata;

import java.util.ArrayList;
import java.util.List;

public class PatientEMRResp {

    private String name;
    private String version;
    private String patientId;
    private boolean isDelete;
    private String createdDate;
    private String modifiedDate;
    private String createrId;
    private String createrName;
    private String modifierId;
    private String modifierName;
    private String id;
    private List<PatientEMRTranslate> patientEMRTranslates;
    private List<PatEMRContentAttachment> pat_EMRContentAttachments;

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getPatientId() {
        return patientId;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public String getCreaterId() {
        return createrId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public String getModifierId() {
        return modifierId;
    }

    public String getModifierName() {
        return modifierName;
    }

    public String getId() {
        return id;
    }

    public List<PatientEMRTranslate> getPatientEMRTranslates() {
        return patientEMRTranslates;
    }

    public List<PatEMRContentAttachment> getPat_EMRContentAttachments() {
        return pat_EMRContentAttachments;
    }

    public static class PatientEMRTranslate {
        private String patientEMRId;
        private String emrContentId;
        private String emrContentAttachmentId;
        private String emrNodeType;
        private String content;
        private String translateLanguage;
        private String translateDate;
        private String translateId;
        private String translateName;
        private String id;

        public String getPatientEMRId() {
            return patientEMRId;
        }

        public String getEmrContentId() {
            return emrContentId;
        }

        public String getEmrContentAttachmentId() {
            return emrContentAttachmentId;
        }

        public String getEmrNodeType() {
            return emrNodeType;
        }

        public String getContent() {
            return content;
        }

        public String getTranslateLanguage() {
            return translateLanguage;
        }

        public String getTranslateDate() {
            return translateDate;
        }

        public String getTranslateId() {
            return translateId;
        }

        public String getTranslateName() {
            return translateName;
        }

        public String getId() {
            return id;
        }
    }

    public static class PatEMRContentAttachment {

        private String id;
        private String emrContentId;
        private String attachmentId;
        private String fileType;
        private String fileName;
        private String url;
        private String createdDate;
        private String fileContent;
        private EMRContent emrContent;

        public String getId() {
            return id;
        }

        public String getEmrContentId() {
            return emrContentId;
        }

        public String getAttachmentId() {
            return attachmentId;
        }

        public String getFileType() {
            return fileType;
        }

        public String getFileName() {
            return fileName;
        }

        public String getUrl() {
            return url;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public String getFileContent() {
            return fileContent;
        }

        public EMRContent getEmrContent() {
            return emrContent;
        }

        public static class EMRContent {

            private String emrNodeypeId;
            private String emrNodeType;
            private String saveDate;
            private String content;
            private String contentType;
            private String uploaderId;
            private String uploaderName;
            private String sourceHospitalId;
            private String sourceHospitalName;
            private String sorceDepartmentId;
            private String sorceDepartmentName;
            private String patientEMRId;
            private String patientEMR;
            private String emrContentAttachments;
            private String id;

            public String getEmrNodeypeId() {
                return emrNodeypeId;
            }

            public String getEmrNodeType() {
                return emrNodeType;
            }

            public String getSaveDate() {
                return saveDate;
            }

            public String getContent() {
                return content;
            }

            public String getContentType() {
                return contentType;
            }

            public String getUploaderId() {
                return uploaderId;
            }

            public String getUploaderName() {
                return uploaderName;
            }

            public String getSourceHospitalId() {
                return sourceHospitalId;
            }

            public String getSourceHospitalName() {
                return sourceHospitalName;
            }

            public String getSorceDepartmentId() {
                return sorceDepartmentId;
            }

            public String getSorceDepartmentName() {
                return sorceDepartmentName;
            }

            public String getPatientEMRId() {
                return patientEMRId;
            }

            public String getPatientEMR() {
                return patientEMR;
            }

            public String getEmrContentAttachments() {
                return emrContentAttachments;
            }

            public String getId() {
                return id;
            }
        }
    }
}
