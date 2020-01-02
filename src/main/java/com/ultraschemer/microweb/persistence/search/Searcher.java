package com.ultraschemer.microweb.persistence.search;

import com.ultraschemer.microweb.domain.error.InvalidLinkConditionInQueryExtensionException;
import com.ultraschemer.microweb.domain.error.QueryParseException;
import com.ultraschemer.microweb.domain.error.QueryTypeUnsupportedException;
import com.ultraschemer.microweb.domain.error.SearchConditionNotFoundException;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.toList;

/**
 * Generic searcher that uses a customized search string, which can be used as a URL query string or a custom search
 * object.
 */
public class Searcher<T> {
    private String entityName;
    private String hardFilter;

    public Searcher(String entityName, String hardFilter) {
        this.entityName = entityName;
        this.hardFilter = hardFilter;
    }

    public Searcher(String entityName) {
        this.entityName = entityName;
        this.hardFilter = null;
    }

    public List<T> load(String query, int start, int count) throws QueryParseException {
        return load(null, query, start, count);
    }

    /**
     * Method which uses the query string parsing algorithm to make a direct search in database, without passing through
     * the intermediary parse from string to search item list.
     *
     * @param parameterConversions The conversion map used to change parameter variable names.
     * @param query The string query per se.
     *        query ::= q=&lt;block&gt; [ ; &lt;block&gt; ...]
     *        &lt;block&gt; ::= &lt;param&gt;:&lt;criterion&gt;:&lt;type&gt;:&lt;value&gt | :OR: | :AND:
     *        &lt;param&gt; ::= STR
     *        &lt;criterion&gt; ::= equals | starts | finishes | lt | gt |
     *        ltequals | gtequals | contains | in
     *        &lt;type&gt; ::=  integer | text |  float | boolean | uuid
     *        &lt;value&gt; :: STR [ ,STR ... ]
     *
     *        Example:
     *        q=User.name:contains:text:Paulo;:AND:;Role.name:starts:text:Admin
     *
     *        Colons can be escaped using the reverse-bar, like:
     *        q=User.name:contains:text:Pau\:lo;:AND:;Role.name:starts:text:Admin
     *
     *        The reverse bar can be used to escape itself, like:
     *        q=User.name:contains:text:Pau\\lo;:AND:;Role.name:starts:text:Admin
     *
     *        If you use the query in a GET URL, or in a post, you need to escape it using url encode, like:
     *        q=urlencode(User.name:contains:text:Paulo;:AND:;Role.name:starts:text:Admin)
     *        which equals to:
     *        q=User.name%3Acontains%3Atext%3APaulo%3B%3AAND%3A%3BRole.name%3Astarts%3Atext%3AAdmin
     * @return The list of searched objects.
     * @throws QueryParseException Raised in the case of search string malformation.
     */
    public List<T> load(Map<String, String> parameterConversions, String query, int start, int count)
            throws QueryParseException {
        try (Session session = EntityUtil.openTransactionSession()) {
            StringBuilder hql = new StringBuilder("from " + entityName + " where ");

            if(hardFilter != null) {
                hql.append("(" + hardFilter + ") and (");
            };

            // Split the individual items using the unescaped semicolons:
            String[] fullItems = query.split("(?<!\\\\);");

            // Make the individual parse of each obtained item:
            LinkedList<Parameter> parameterLinkedList = new LinkedList<>();
            for(String item: fullItems) {
                if(":OR:".equals(item)) {
                    hql.append(" or ");
                } else if(":AND:".equals(item)) {
                    hql.append(" and ");
                } else {
                    // Split the items using the unescaped colons:
                    String[] subItems = item.split("(?<!\\\\):");
                    if(subItems.length != 4) {
                        String msg;
                        msg = "Invalid format - the query item must have exactly four colon (:) separated elements.";
                        throw new QueryParseException(msg);
                    }

                    String field = subItems[0].replaceAll("\\\\(.?)", "$1");
                    String criterion = subItems[1].replaceAll("\\\\(.?)", "$1");
                    String type = subItems[2].replaceAll("\\\\(.?)", "$1");
                    // Don't revert the escaping here, since the values can be evaluated
                    // in the "in" criterion, yet.
                    String value = subItems[3];

                    // Convert the parameter in according to the conversion map:
                    if(parameterConversions!= null) {
                        String convertTo = parameterConversions.get(field);
                        if (convertTo != null) {
                            field = convertTo;
                        }
                    }

                    if(!Arrays.asList(new String [] {"equals",
                            "starts", "finishes", "lt", "gt",
                            "ltequals", "gtequals", "contains",
                            "in"}).contains(criterion))
                    {
                        String msg = "Criterion \"" + criterion + "\" not supported by search.";
                        throw new QueryParseException(msg);
                    }

                    // Finally create the search item:
                    Parameter parameter;
                    switch(type) {
                        case "integer":
                            if(criterion.toLowerCase().equals("in")) {
                                // Split parameters by comma:
                                parameter = new Parameter<>(field, criterion,
                                        Arrays.stream(value.split("(?<!\\\\),"))
                                                // Reverse the escapes:
                                                .map(e -> parseLong(e.replaceAll("\\\\(.?)", "$1")))
                                                .collect(toList()));
                            } else {
                                parameter = new Parameter<>(field, criterion,
                                        parseLong(value.replaceAll("\\\\(.?)", "$1")));
                            }
                            break;
                        case "text":
                            if(criterion.toLowerCase().equals("in")) {
                                // Split parameters by comma:
                                parameter = new Parameter<>(field, criterion,
                                        Arrays.stream(value.split("(?<!\\\\),"))
                                                // Reverse the escapes:
                                                .map(e -> e.replaceAll("\\\\(.?)", "$1"))
                                                .collect(toList()));
                            } else {
                                parameter = new Parameter<>(field, criterion, value.replaceAll("\\\\(.?)", "$1"));
                            }
                            break;
                        case "float":
                            if(criterion.toLowerCase().equals("in")) {
                                // Split parameters by comma:
                                parameter = new Parameter<>(field, criterion,
                                        Arrays.stream(value.split("(?<!\\\\),"))
                                                // Reverse the escapes:
                                                .map(e -> parseDouble(e.replaceAll("\\\\(.?)", "$1")))
                                                .collect(toList()));
                            } else {
                                parameter = new Parameter<>(field, criterion,
                                        parseDouble(value.replaceAll("\\\\(.?)", "$1")));
                            }
                            break;
                        case "uuid":
                            if(criterion.toLowerCase().equals("in")) {
                                // Split parameters by comma:
                                parameter = new Parameter<>(field, criterion,
                                        Arrays.stream(value.split("(?<!\\\\),"))
                                                // Reverse the escapes:
                                                .map(e -> UUID.fromString(e.replaceAll("\\\\(.?)", "$1")))
                                                .collect(toList()));
                            } else {
                                parameter = new Parameter<>(field, criterion,
                                        UUID.fromString(value.replaceAll("\\\\(.?)", "$1")));
                            }
                            break;
                        case "boolean":
                            if(criterion.toLowerCase().equals("in")) {
                                throw new QueryParseException("Boolean types doesn't support the \"in\" criterion.");
                            } else {
                                parameter = new Parameter<>(field, criterion, Boolean.parseBoolean(value));
                            }
                            break;
                        default: {
                            String msg = "Data type \"" + type + "\"not supported by search.";
                            throw new QueryParseException(msg);
                        }
                    }

                    switch (parameter.getCriterion().toLowerCase()) {
                        case "contains":
                        case "starts":
                        case "finishes":
                            hql.append("lower(");
                            hql.append(parameter.getField());
                            hql.append(") like :");
                            break;
                        case "equals":
                            hql.append(parameter.getField());
                            hql.append(" = :");
                            break;
                        case "lt":
                            hql.append(parameter.getField());
                            hql.append(" < :");
                            break;
                        case "gt":
                            hql.append(parameter.getField());
                            hql.append(" > :");
                            break;
                        case "ltequals":
                            hql.append(parameter.getField());
                            hql.append(" <= :");
                            break;
                        case "gtequals":
                            hql.append(parameter.getField());
                            hql.append(" >= :");
                            break;
                        case "in":
                            hql.append(parameter.getField());
                            hql.append(" in :");
                            break;
                        default:
                            String message = "Criterion " + parameter.getCriterion() + " is not supported by search.";
                            throw new SearchConditionNotFoundException(message);
                    }

                    hql.append(parameter.getField().replaceAll("\\.", "__POINT__"));
                    parameterLinkedList.add(parameter);
                }
            }

            // Close the custom query:
            if(hardFilter != null) {
                hql.append(")");
            }

            // Finally, create the query:
            Query databaseQuery = session.createQuery(hql.toString());

            for(Parameter parameter: parameterLinkedList) {
                Object value = parameter.getValue();
                String field = parameter.getField().replaceAll("\\.", "__POINT__");
                switch (parameter.getCriterion().toLowerCase()) {
                    case "contains":
                        databaseQuery.setParameter(field, "%" + value.toString().toLowerCase() + "%");
                        break;
                    case "starts":
                        databaseQuery.setParameter(field, value.toString().toLowerCase() + "%");
                        break;
                    case "finishes":
                        databaseQuery.setParameter(field, "%" + value.toString().toLowerCase());
                        break;
                    case "in":
                        databaseQuery.setParameterList(field, (List)value);
                        break;
                    default:
                        databaseQuery.setParameter(field, value);
                }
            }

            @SuppressWarnings("unchecked")
            List<T> ts = databaseQuery.setFirstResult(start).setMaxResults(count).list();

            return ts;
        } catch (Exception e) {
            String msg = "Error interpreting or executing query string: " + e.getMessage();
            throw new QueryParseException(msg);
        }
    }

    public static String createQuery(String param, String criterion, String type, List<Object> value)
            throws StandardException {
        String newQuery = param + ":" + criterion + ":" + type + ":";

        // Evaluate value
        switch(type) {
            case "integer":
            case "float":
            case "uuid":
            case "boolean":
                newQuery += value.stream().map(Object::toString).collect(Collectors.joining(","));
                break;
            case "text":
                // Text values must have the reversed bars (\), semicolons (;), colons (:) and commas (,) escaped:
                newQuery += value.stream().map(e -> e.toString()
                        .replaceAll("\\\\", "\\\\")
                        .replaceAll(";", "\\;")
                        .replaceAll(":", "\\:")
                        .replaceAll(",", "\\,")).collect(Collectors.joining(","));
            default:
                throw new QueryTypeUnsupportedException("The type " + type +
                        " is not supported to create search queries.");
        }

        return newQuery;
    }

    public static String extendQuery(String query, String linkCondition, String param, String criterion, String type, List<Object> value)
            throws StandardException {
        String newQuery = query;
        if(!query.endsWith(";")) {
            newQuery += ";";
        }

        if(Arrays.asList("AND", "OR").contains(linkCondition.toUpperCase())) {
            return newQuery + ":" + linkCondition.toUpperCase() +  ":;" + createQuery(param, criterion, type, value);
        } else {
            throw new InvalidLinkConditionInQueryExtensionException(
                    "Only the conditions \"OR\" and \"AND\" are valid ones.");
        }
    }
}
