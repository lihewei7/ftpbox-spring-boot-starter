package io.github.lihewei7.ftpbox.core;

public class UnixPath {

    private String path;

    public UnixPath(String path) {
        if (path == null) {
            path = "";
        }
        this.path = path;
    }

    public UnixPath append(String other) {
        if (other == null) {
            return this;
        }

        if (path.length() == 0) {
            path = other;
            return this;
        }

        if (path.endsWith("/")) {
            if (other.startsWith("/")) {
                path = path.substring(0, path.length() - 1) + other;
            } else {
                path = path + other;
            }
        } else {
            if (other.startsWith("/")) {
                path = path + other;
            } else {
                path = path + "/" + other;
            }
        }
        return this;
    }

    public UnixPath append(UnixPath path) {
        return append(path.path);
    }

    public String getFileName() {
        int index = path.lastIndexOf("/");
        if (index != -1) {
            return path.substring(index + 1);
        }
        return path;
    }

    public String getDir() {
        int index = path.lastIndexOf("/");
        if (index != -1) {
            return path.substring(0, index + 1);
        }
        return "";
    }

    @Override
    public String toString() {
        return path;
    }

    public static UnixPath getPath(String path, String... paths) {
        UnixPath p = new UnixPath(path);
        for (String pi : paths) {
            p.append(pi);
        }
        return p;
    }
}
