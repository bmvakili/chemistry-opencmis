/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.chemistry.opencmis.inmemory.query;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.apache.chemistry.opencmis.server.support.query.CalendarHelper;
import org.apache.chemistry.opencmis.server.support.query.CmisQlStrictLexer;
import org.apache.chemistry.opencmis.server.support.query.CmisQlStrictParser;
import org.apache.chemistry.opencmis.server.support.query.CmisQlStrictParser_CmisBaseGrammar.query_return;
import org.apache.chemistry.opencmis.server.support.query.CmisQueryWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractQueryConditionProcessor implements QueryConditionProcessor {

    private static Log LOG = LogFactory.getLog(ProcessQueryTest.class);
    abstract public void onStartProcessing(Tree whereNode);
    abstract public void onStopProcessing();

    // Compare operators
    abstract public void onEquals(Tree eqNode, Tree leftNode, Tree rightNode);
    abstract public void onNotEquals(Tree neNode, Tree leftNode, Tree rightNode);
    abstract public void onGreaterThan(Tree gtNode, Tree leftNode, Tree rightNode);
    abstract public void onGreaterOrEquals(Tree geNode, Tree leftNode, Tree rightNode);
    abstract public void onLessThan(Tree ltNode, Tree leftNode, Tree rightNode);
    abstract public void onLessOrEquals(Tree leqNode, Tree leftNode, Tree rightNode);

    // Boolean operators
    public void onPreNot(Tree opNode, Tree leftNode) {
    }
    abstract public void onNot(Tree opNode, Tree leftNode);
    public void onPostNot(Tree opNode, Tree leftNode) {
    }
    public void onPreAnd(Tree opNode, Tree leftNode, Tree rightNode) {
    }
    abstract public void onAnd(Tree opNode, Tree leftNode, Tree rightNode);
    public void onPostAnd(Tree opNode, Tree leftNode, Tree rightNode) {
    }
    public void onPreOr(Tree opNode, Tree leftNode, Tree rightNode) {
    }
    abstract public void onOr(Tree opNode, Tree leftNode, Tree rightNode);
    public void onPostOr(Tree opNode, Tree leftNode, Tree rightNode) {
    }

    // Multi-value:
    abstract public void onIn(Tree node, Tree colNode, Tree listNode);
    abstract public void onNotIn(Tree node, Tree colNode, Tree listNode);
    abstract public void onInAny(Tree node, Tree colNode, Tree listNode);
    abstract public void onNotInAny(Tree node, Tree colNode, Tree listNode);
    abstract public void onEqAny(Tree node, Tree literalNode, Tree colNode);

    // Null comparisons:
    abstract public void onIsNull(Tree nullNode, Tree colNode);
    abstract public void onIsNotNull(Tree notNullNode, Tree colNode);

    // String matching:
    abstract public void onIsLike(Tree node, Tree colNode, Tree stringNode);
    abstract public void onIsNotLike(Tree node, Tree colNode, Tree stringNode);

    // Functions:
    abstract public void onContains(Tree node, Tree colNode, Tree paramNode);
    abstract public void onInFolder(Tree node, Tree colNode, Tree paramNode);
    abstract public void onInTree(Tree node, Tree colNode, Tree paramNode);
    abstract public void onScore(Tree node);

    // convenience method because everybody needs this piece of code
    static public CmisQueryWalker getWalker(String statement) throws UnsupportedEncodingException, IOException, RecognitionException {
        CharStream input = new ANTLRInputStream(new ByteArrayInputStream(statement.getBytes("UTF-8")));
        TokenSource lexer = new CmisQlStrictLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        CmisQlStrictParser parser = new CmisQlStrictParser(tokens);
        CommonTree parserTree; // the ANTLR tree after parsing phase

        query_return parsedStatement = parser.query();
//        if (parser.errorMessage != null) {
//            throw new RuntimeException("Cannot parse query: " + statement + " (" + parser.errorMessage + ")");
//        }
        parserTree = (CommonTree) parsedStatement.getTree();

        CommonTreeNodeStream nodes = new CommonTreeNodeStream(parserTree);
        nodes.setTokenStream(tokens);
        CmisQueryWalker walker = new CmisQueryWalker(nodes);
        return walker;
    }


    // Base interface called from query parser
    public Boolean walkPredicate(Tree whereNode) {
        if (null != whereNode) {
            onStartProcessing(whereNode);
            evalWhereNode(whereNode);
            onStopProcessing();
        }
        return null; // unused 
    }

    // ///////////////////////////////////////////////////////
    // Processing the WHERE clause

    protected void evalWhereNode(Tree node) {
        // Ensure that we receive only valid tokens and nodes in the where
        // clause:
        LOG.debug("evaluating node: " + node.toString());
        switch (node.getType()) {
        case CmisQlStrictLexer.WHERE:
            break; // ignore
        case CmisQlStrictLexer.EQ:
            evalWhereNode(node.getChild(0));
            onEquals(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;
        case CmisQlStrictLexer.NEQ:
            evalWhereNode(node.getChild(0));
            onNotEquals(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;
        case CmisQlStrictLexer.GT:
            evalWhereNode(node.getChild(0));
            onGreaterThan(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;
        case CmisQlStrictLexer.GTEQ:
            evalWhereNode(node.getChild(0));
            onGreaterOrEquals(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;
        case CmisQlStrictLexer.LT:
            evalWhereNode(node.getChild(0));
            onLessThan(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;
        case CmisQlStrictLexer.LTEQ:
            evalWhereNode(node.getChild(0));
            onLessOrEquals(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;

        case CmisQlStrictLexer.NOT:
            onPreNot(node, node.getChild(0));
            onNot(node, node.getChild(0));
            evalWhereNode(node.getChild(0));
            onPostNot(node, node.getChild(0));
            break;
        case CmisQlStrictLexer.AND:
            onPreAnd(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(0));
            onAnd(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            onPostAnd(node, node.getChild(0), node.getChild(1));
            break;
        case CmisQlStrictLexer.OR:
            onPreOr(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(0));
            onOr(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            onPostOr(node, node.getChild(0), node.getChild(1));
            break;

        // Multi-value:
        case CmisQlStrictLexer.IN:
            evalWhereNode(node.getChild(0));
            onIn(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;
        case CmisQlStrictLexer.NOT_IN:
            evalWhereNode(node.getChild(0));
            onNotIn(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;
        case CmisQlStrictLexer.IN_ANY:
            evalWhereNode(node.getChild(0));
            onInAny(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;
        case CmisQlStrictLexer.NOT_IN_ANY:
            evalWhereNode(node.getChild(0));
            onNotInAny(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;
        case CmisQlStrictLexer.EQ_ANY:
            evalWhereNode(node.getChild(0));
            onEqAny(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;

        // Null comparisons:
        case CmisQlStrictLexer.IS_NULL:
            onIsNull(node, node.getChild(0));
            evalWhereNode(node.getChild(0));
            break;
        case CmisQlStrictLexer.IS_NOT_NULL:
            onIsNotNull(node, node.getChild(0));
            evalWhereNode(node.getChild(0));
            break;

        // String matching
        case CmisQlStrictLexer.LIKE:
            evalWhereNode(node.getChild(0));
            onIsLike(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;
        case CmisQlStrictLexer.NOT_LIKE:
            evalWhereNode(node.getChild(0));
            onIsNotLike(node, node.getChild(0), node.getChild(1));
            evalWhereNode(node.getChild(1));
            break;

        // Functions
        case CmisQlStrictLexer.CONTAINS:
            if (node.getChildCount() == 1) {
                onContains(node, null, node.getChild(0));
                evalWhereNode(node.getChild(0));
            } else {
                evalWhereNode(node.getChild(0));
                onContains(node, node.getChild(0), node.getChild(1));
                evalWhereNode(node.getChild(1));
            }
            break;
        case CmisQlStrictLexer.IN_FOLDER:
            if (node.getChildCount() == 1) {
                onInFolder(node, null, node.getChild(0));
                evalWhereNode(node.getChild(0));
            } else {
                evalWhereNode(node.getChild(0));
                onInFolder(node, node.getChild(0), node.getChild(1));
                evalWhereNode(node.getChild(1));
            }
            break;
        case CmisQlStrictLexer.IN_TREE:
            if (node.getChildCount() == 1) {
                onInTree(node, null, node.getChild(0));
                evalWhereNode(node.getChild(0));
            } else {
                evalWhereNode(node.getChild(0));
                onInTree(node, node.getChild(0), node.getChild(1));
                evalWhereNode(node.getChild(1));
            }
            break;
        case CmisQlStrictLexer.SCORE:
            onScore(node);
            break;

        default:
            // do nothing;
        }
    }

    // helper functions that are needed by most query tree walkers

    protected Object onLiteral(Tree node) {
        int type = node.getType();
        String text = node.getText();
        switch (type) {
        case CmisQlStrictLexer.BOOL_LIT:
            return Boolean.parseBoolean(node.getText());
        case CmisQlStrictLexer.NUM_LIT:
            if (text.contains(".") || text.contains("e") || text.contains("E"))
                return Double.parseDouble(text);
            else
                return Long.parseLong(text);
        case CmisQlStrictLexer.STRING_LIT:
            return text.substring(1, text.length()-1);
        case CmisQlStrictLexer.TIME_LIT:
            GregorianCalendar gc = CalendarHelper.fromString(text.substring(text.indexOf('\'')+1, text.lastIndexOf('\'')));
            return gc;
        default:
            throw new RuntimeException("Unknown literal. " + node);
        }
    }

    protected List<Object> onLiteralList(Tree node) {
        List<Object> res = new ArrayList<Object>(node.getChildCount());
        for (int i=0; i<node.getChildCount(); i++) {
            Tree literal =  node.getChild(i);
            res.add(onLiteral(literal));
        }
        return res;
    }

}
