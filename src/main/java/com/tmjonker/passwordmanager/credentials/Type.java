package com.tmjonker.passwordmanager.credentials;

public enum Type {

    WEBSITE {
        @Override
        public String toString() {
            return "Website";
        }
    }
    ,
    APPLICATION {
        @Override
        public String toString() {
            return "Applications";
        }
    }
    ,
    EMAIL {
        @Override
        public String toString() {
            return "Email";
        }
    }
    ,
    FINANCIAL {
        @Override
        public String toString() {
            return "Financial";
        }
    }
    ,
    GAME {
        @Override
        public String toString() {
            return "Games";
        }
    }
}
