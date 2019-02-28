package com.ultraschemer.microweb.persistence.search;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.domain.error.QueryParseException;
import com.ultraschemer.microweb.domain.error.SearchConditionNotFoundException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.ultraschemer.microweb.persistence.search.Condition.AND;
import static com.ultraschemer.microweb.persistence.search.Condition.OR;

/**
 * Generic searcher that uses a customized search string, which can be used as a URL query string or a custom search
 * object.
 */
public class Searcher<T> {
    private String entityName;

    public Searcher(String entityName) {
        this.entityName = entityName;
    }

    public List<T> load(Item... items) throws SearchConditionNotFoundException, QueryParseException {
        return load(null, items);
    }

    public List<T> load(Map<String, String> parameterConversions, Item... items)
            throws SearchConditionNotFoundException, QueryParseException {
        List<T> ts = null;

        StringBuilder hql = new StringBuilder("from " + entityName + " where ");
        for(Item item : items) {
            if(item instanceof Parameter) {
                Parameter parameter = (Parameter) item;
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
                    // TODO: Add the criterion "in", which is in absent.
                    default:
                        String message = "Search criterion " + parameter.getCriterion() + " is not supported.";
                        throw new SearchConditionNotFoundException(message);
                }
                hql.append(parameter.getField());
            } else {
                Condition condition = (Condition) item;
                hql.append(condition.toString());
            }
        }

        // Gera a query e a executa:
        try(Session session = EntityUtil.openTransactionSession()) {
            Query query = session.createQuery(hql.toString());

            for(Item item : items) {
                if(item instanceof Parameter) {
                    Parameter parameter = (Parameter) item;
                    Object value = parameter.getValue();
                    switch (parameter.getCriterion().toLowerCase()) {
                        case "contains":
                            query.setParameter(parameter.getField(),
                                    "%" + parameter.getValue().toString().toLowerCase() + "%");
                            break;
                        case "starts":
                            query.setParameter(parameter.getField(),
                                    parameter.getValue().toString().toLowerCase() + "%");
                            break;
                        case "finishes":
                            query.setParameter(parameter.getField(),
                                    "%" + parameter.getValue().toString().toLowerCase());
                            break;
                        default:
                            query.setParameter(parameter.getField(), value);
                    }
                }
            }

            @SuppressWarnings("unchecked")
            List<T> tss = query.list();
            ts = tss;
        } catch (Exception e) {
            String msg = "Error trying to evaluate the query string: " + e.getLocalizedMessage() +
                    "\nStack-Trace: " + Throwables.getStackTraceAsString(e);
            throw new QueryParseException(msg);
        }

        return ts;
    }

    public static Item[] parseQueryString(String query) throws QueryParseException {
        return parseQueryString(null, query);
    }

    /**
     * This method parses a URL search query string, tokenizing it, using the next semantics:
     *
     * // TODO: Support searches in a list of elements ("in" criterion).
     *
     * query ::= q=&lt;block&gt; [ ; &lt;block&gt; ...]
     * &lt;block&gt; ::= &lt;param&gt;:&lt;criterion&gt;:&lt;type&gt;:&lt;value&gt | :OR: | :AND:
     * &lt;param&gt; ::= STR
     * &lt;criterion&gt; ::= equals | starts | finishes | lt | gt |
     * ltequals | gtequals | contains | in
     * &lt;type&gt; ::=  integer | text |  float | boolean
     * &lt;value&gt; :: STR [ ,STR ... ]
     *
     * Example:
     * q=User.name:contains:text:Paulo;:AND:;Role.name:starts:text:Admin
     *
     * @param parameterConversions Map containing the names necessary to change, from the query string to the item array.
     * @param query A string containing a query.
     * @return An array with the item tokens of the given query.
     *
     */
    public static Item[] parseQueryString(Map<String, String> parameterConversions, String query)
            throws QueryParseException {
        try {
            LinkedList<Item> itemLinkedList = new LinkedList<>();
            // The individual Items:
            String fullItems[] = query.split(";");

            // Make the individual parse of each obtained item:
            for(String item: fullItems) {
                if(":OR:".equals(item)) {
                    itemLinkedList.add(OR);
                } else if(":AND:".equals(item)) {
                    itemLinkedList.add(AND);
                } else {
                    String subitems[] = item.split(":");
                    if(subitems.length != 4) {
                        String msg;
                        msg = "Invalid format - the query item must have exactly four colon (:) separated elements.";
                        throw new QueryParseException(msg);
                    }

                    String field = subitems[0];
                    String criterion = subitems[1];
                    String type = subitems[2];
                    String value = subitems[3];

                    // Convert parameter in according to conversion map:
                    String convertTo = parameterConversions.get(field);
                    if(convertTo != null) {
                        field = convertTo;
                    }

                    if(!Arrays.asList(new String [] {"equals",
                            "starts", "finishes", "lt", "gt",
                            "ltequals", "gtequals", "contains"}).contains(criterion)) {
                        String msg = "Search doesn't support this criterion: \"" + criterion + "\".";
                        throw new QueryParseException(msg);
                    }

                    // Finally create the parameter item:
                    Parameter parameter;
                    switch(type) {
                        case "integer":
                            parameter = new Parameter<>(field, criterion, Long.parseLong(value));
                            break;
                        case "text":
                            parameter = new Parameter<>(field, criterion, value);
                            break;
                        case "float":
                            parameter = new Parameter<>(field, criterion, Double.parseDouble(value));
                            break;
                        case "boolean":
                            parameter = new Parameter<>(field, criterion, Boolean.parseBoolean(value));
                            break;
                        default: {
                            String msg = "Data type \"" + type + "\"not suppported by search.";
                            throw new QueryParseException(msg);
                        }
                    }

                    itemLinkedList.add(parameter);
                }
            }

            return itemLinkedList.toArray(new Item[]{});
        } catch (Exception e) {
            String msg = "Error interpreting query string: " + e.getMessage();
            throw new QueryParseException(msg);
        }
    }

    public List<T> load(String query) throws QueryParseException {
        return load(null, query);
    }

    /**
     * Method which uses the query string parsing algorithm to make a direct search in database, without passing through
     * the intermediary parse from string to search item list.
     *
     * @param parameterConversions The conversion map used to change parameter variable names.
     * @param query The string query per se.
     * @return The list of searched objects.
     * @throws QueryParseException Raised in the case of search string malformation.
     */
    public List<T> load(Map<String, String> parameterConversions, String query) throws QueryParseException {
        try (Session session = EntityUtil.openTransactionSession()) {
            LinkedList<Parameter> parameterLinkedList = new LinkedList<>();
            StringBuilder hql = new StringBuilder("from " + entityName + " where ");

            // The individual Items:
            String fullItems[] = query.split(";");

            // Make the individual parse of each obtained item:
            for(String item: fullItems) {
                if(":OR:".equals(item)) {
                    hql.append(" or ");
                } else if(":AND:".equals(item)) {
                    hql.append(" and ");
                } else {
                    String subitems[] = item.split(":");
                    if(subitems.length != 4) {
                        String msg;
                        msg = "Invalid format - the query item must have exactly four colon (:) separated elements.";
                        throw new QueryParseException(msg);
                    }

                    String field = subitems[0];
                    String criterion = subitems[1];
                    String type = subitems[2];
                    String value = subitems[3];

                    // Convert the parameter in according to the conversion map:
                    String convertTo = parameterConversions.get(field);
                    if(convertTo != null) {
                        field = convertTo;
                    }

                    if(!Arrays.asList(new String [] {"equals",
                            "starts", "finishes", "lt", "gt",
                            "ltequals", "gtequals", "contains"}).contains(criterion)) {
                        String msg = "Criterion \"" + criterion + "\" not supported by search.";
                        throw new QueryParseException(msg);
                    }

                    // Finally create the search item:
                    Parameter parameter;
                    switch(type) {
                        case "integer":
                            parameter = new Parameter<>(field, criterion, Long.parseLong(value));
                            break;
                        case "text":
                            parameter = new Parameter<>(field, criterion, value);
                            break;
                        case "float":
                            parameter = new Parameter<>(field, criterion, Double.parseDouble(value));
                            break;
                        case "boolean":
                            parameter = new Parameter<>(field, criterion, Boolean.parseBoolean(value));
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
                        // TODO: Add the criterion "in", which is absent.
                        default:
                            String message = "Criterion " + parameter.getCriterion() + " is not supported by search.";
                            throw new SearchConditionNotFoundException(message);
                    }

                    hql.append(parameter.getField());
                    parameterLinkedList.add(parameter);
                }
            }

            @SuppressWarnings("unchecked")
            Query databaseQuery = session.createQuery(hql.toString());

            for(Parameter parameter: parameterLinkedList) {
                Object value = parameter.getValue();
                switch (parameter.getCriterion().toLowerCase()) {
                    case "contains":
                        databaseQuery.setParameter(parameter.getField(),
                                "%" + parameter.getValue().toString().toLowerCase() + "%");
                        break;
                    case "starts":
                        databaseQuery.setParameter(parameter.getField(),
                                parameter.getValue().toString().toLowerCase() + "%");
                        break;
                    case "finishes":
                        databaseQuery.setParameter(parameter.getField(),
                                "%" + parameter.getValue().toString().toLowerCase());
                        break;
                    default:
                        databaseQuery.setParameter(parameter.getField(), value);
                }
            }

            @SuppressWarnings("unchecked")
            List<T> ts = databaseQuery.list();

            return ts;
        } catch (Exception e) {
            String msg = "Error interpreting or executing query string: " + e.getMessage();
            throw new QueryParseException(msg);
        }
    }
}
