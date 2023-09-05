package io.github.compendiummc.shelf.features;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.logging.log4j.core.impl.ExtendedStackTraceElement;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.util.StackLocatorUtil;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@TargetClass(className = "org.apache.logging.log4j.core.impl.ThrowableProxy")
public final class Target_org_apache_logging_log4j_core_impl_ThrowableProxy {

  @Alias
  private Throwable throwable;
  @Alias
  private String name;
  @Alias
  private String message;
  @Alias
  private String localizedMessage;
  @Alias
  private ExtendedStackTraceElement[] extendedStackTrace;
  @Alias
  private Target_org_apache_logging_log4j_core_impl_ThrowableProxy causeProxy;
  @Alias
  private ThrowableProxy[] suppressedProxies;

  @Substitute
  Target_org_apache_logging_log4j_core_impl_ThrowableProxy(final Throwable throwable, final Set<Throwable> visited) {
    this.throwable = throwable;
    this.name = throwable.getClass().getName();
    this.message = throwable.getMessage();
    this.localizedMessage = throwable.getLocalizedMessage();
    final Map<String, ?> map = new HashMap<>();
    final Deque<Class<?>> stack = StackLocatorUtil.getCurrentStackTrace();
    this.extendedStackTrace = Target_org_apache_logging_log4j_core_impl_ThrowableProxyHelper.toExtendedStackTrace(this, stack, map, null, throwable.getStackTrace());
    final Throwable throwableCause = throwable.getCause();
    final Set<Throwable> causeVisited = new HashSet<>(1);
    this.causeProxy = throwableCause == null ? null : new Target_org_apache_logging_log4j_core_impl_ThrowableProxy(throwable, stack, map, throwableCause,
        visited, causeVisited, 0);
    this.suppressedProxies = Target_org_apache_logging_log4j_core_impl_ThrowableProxyHelper.toSuppressedProxies(throwable, visited);
  }

  private Target_org_apache_logging_log4j_core_impl_ThrowableProxy(final Throwable parent, final Deque<Class<?>> stack,
                                                                   final Map<String, ?> map,
                                                                   final Throwable cause, final Set<Throwable> suppressedVisited,
                                                                   final Set<Throwable> causeVisited,
                                                                   int depth) {
    causeVisited.add(cause);
    this.throwable = cause;
    this.name = cause.getClass().getName();
    this.message = this.throwable.getMessage();
    this.localizedMessage = this.throwable.getLocalizedMessage();
    this.extendedStackTrace = Target_org_apache_logging_log4j_core_impl_ThrowableProxyHelper.toExtendedStackTrace(this, stack, map, parent.getStackTrace(), cause.getStackTrace());
    final Throwable causeCause = cause.getCause();
    this.causeProxy = causeCause == null || causeVisited.contains(causeCause) || depth > 5 ? null : new Target_org_apache_logging_log4j_core_impl_ThrowableProxy(parent,
        stack, map, causeCause, suppressedVisited, causeVisited, depth + 1);
    this.suppressedProxies = Target_org_apache_logging_log4j_core_impl_ThrowableProxyHelper.toSuppressedProxies(cause, suppressedVisited);
  }


  @TargetClass(className = "org.apache.logging.log4j.core.impl.ThrowableProxyHelper")
  public static final class Target_org_apache_logging_log4j_core_impl_ThrowableProxyHelper {

    @Alias
    public static ExtendedStackTraceElement[] toExtendedStackTrace(
        Target_org_apache_logging_log4j_core_impl_ThrowableProxy proxy,
        Deque<Class<?>> stack,
        Map<String, ?> cache,
        StackTraceElement[] rootTrace,
        StackTraceElement[] stackTrace
    ) {
      return null;
    }

    @Alias
    public static ThrowableProxy[] toSuppressedProxies(Throwable throwable, Set<Throwable> visited) {
      return null;
    }
  }
}
