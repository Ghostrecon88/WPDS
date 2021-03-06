/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *  
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Johannes Spaeth - initial API and implementation
 *******************************************************************************/
package typestate.impl.statemachines;

import java.util.Collection;
import java.util.Set;

import boomerang.WeightedForwardQuery;
import soot.SootMethod;
import soot.Unit;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class PipedInputStreamStateMachine extends TypeStateMachineWeightFunctions {

    public static enum States implements State {
        INIT, CONNECTED, ERROR;

        @Override
        public boolean isErrorState() {
            return this == ERROR;
        }

        @Override
        public boolean isInitialState() {
            return false;
        }

        @Override
        public boolean isAccepting() {
            return false;
        }
    }

    public PipedInputStreamStateMachine() {
        addTransition(new MatcherTransition(States.INIT, connect(), Parameter.This, States.CONNECTED, Type.OnReturn));
        addTransition(new MatcherTransition(States.INIT, readMethods(), Parameter.This, States.ERROR, Type.OnReturn));
        addTransition(new MatcherTransition(States.CONNECTED, readMethods(), Parameter.This, States.CONNECTED,
                Type.OnReturn));
        addTransition(new MatcherTransition(States.ERROR, readMethods(), Parameter.This, States.ERROR, Type.OnReturn));
    }

    private Set<SootMethod> connect() {
        return selectMethodByName(getSubclassesOf("java.io.PipedInputStream"), "connect");
    }

    private Set<SootMethod> readMethods() {
        return selectMethodByName(getSubclassesOf("java.io.PipedInputStream"), "read");
    }

    @Override
    public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(SootMethod m, Unit unit) {
        return generateAtAllocationSiteOf(m, unit, java.io.PipedInputStream.class);
    }

    @Override
    protected State initialState() {
        return States.INIT;
    }
}
