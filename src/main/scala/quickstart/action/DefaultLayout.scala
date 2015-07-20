package quickstart.action

import xitrum.{Action, SkipCsrfCheck}

trait DefaultLayout extends Action with SkipCsrfCheck{
  override def layout = renderViewNoLayout[DefaultLayout]()
}
