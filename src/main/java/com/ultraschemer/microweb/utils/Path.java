package com.ultraschemer.microweb.utils;

public class Path {
    /**
     * Evaluate path equivalence.
     *
     * Two paths are equivalent if they differ from each other only by input parameter names.
     * These input parameter names are identified by colons, and then a continuous string, formed
     * only by word letters, numbers and underline. In a more general algorithm, the only character
     * not allowed in a parameter name is the bar (/)
     *
     * @param p1 The first path to evaluate
     * @param p2 THe second path to evaluate
     * @return Equals true, if the paths are equivalent, false, otherwise.
     */
    public static boolean areEquivalent(String p1, String p2) {
        // Remove trailing bars:
        String path1 = p1.replaceAll("/+$", "");
        String path2 = p2.replaceAll("/+$", "");

        if(path1.equals("")) {
            path1 = "/";
        }

        if(path2.equals("")) {
            path2 = "/";
        }

        int i1 = 0, i2 = 0;
        for(; i1 < path1.length() && i2 < path2.length(); i1++, i2++) {
            if(i1 > 0 && i2 > 0 &&
               ((path1.charAt(i1) == ':' && path1.charAt(i1-1) == '/') ||
                (path2.charAt(i2) == ':' && path2.charAt(i2-1) == '/')))
            {
                while(path1.charAt(i1) != '/') {
                    i1++;

                    if(i1 >= path1.length()) {
                        break;
                    }
                }

                while(path2.charAt(i2) != '/') {
                    i2++;

                    if(i2 >= path2.length()) {
                        break;
                    }
                }
            }

            if(i1 >= path1.length() && i2 >= path2.length()) {
                break;
            } else if(i1 >= path1.length()) {
                return false;
            } else if(i2 >= path2.length()) {
                return false;
            }

            if(path1.charAt(i1) != path2.charAt(i2)) {
                return false;
            }
        }

        return i1 >= path1.length() && i2 >= path2.length();
    }
}
