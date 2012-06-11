import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (activeWindow != null)
		{
		    IWorkbenchPage activePage = activeWindow.getActivePage();

		    if (activePage != null)
		    {
		        activePage.addPartListener(new PartListener());
		    }
		    else
		    {
		        activeWindow.addPageListener(new PageListener());
		    }
		}
		else
		{
		    for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
		    {
		        for (IWorkbenchPage page : window.getPages()) {
		            page.addPartListener(new PartListener());
		        }
		        window.addPageListener(new PageListener());
		    }

		    PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
				
		    	@Override
				public void windowOpened(IWorkbenchWindow window) {
					System.out.println("windowOpened");
				}
				@Override
				public void windowDeactivated(IWorkbenchWindow window) {
					System.out.println("windowDeactivated");
				}

				@Override
				public void windowClosed(IWorkbenchWindow window) {
					System.out.println("windowClosed");
				}

				@Override
				public void windowActivated(IWorkbenchWindow window) {
					System.out.println("windowActivated");
				}
			});
		}       
		
		
		//PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPageListener(new PageListener());
		/*
		PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
			
			@Override
			public void windowOpened(IWorkbenchWindow window) {
				System.out.println("windowOpened");
			}
			@Override
			public void windowDeactivated(IWorkbenchWindow window) {
				System.out.println("windowDeactivated");
			}

			@Override
			public void windowClosed(IWorkbenchWindow window) {
				System.out.println("windowClosed");
			}

			@Override
			public void windowActivated(IWorkbenchWindow window) {
				System.out.println("windowActivated");
			}
		});
		*/
	}
}			
		
		
		/*
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
		page.addPartListener(new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				System.out.println("partOpened");
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				System.out.println("partDeactivated");
				
			}
			
			@Override
			public void partClosed(IWorkbenchPart part) {
				System.out.println("partClosed");
				
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				System.out.println("partBroughtToTop");
				
			}
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				System.out.println("partActivated");
				
				
				if (part instanceof IEditorPart) {
					IEditorInput s =  ((IEditorPart) part).getEditorInput();
					IFile file = (IFile) s.getAdapter(IFile.class);
					if (file != null) {
					    try {
							InputStream stream = file.getContents();

							final char[] buffer = new char[0x10000];
							StringBuilder out = new StringBuilder();
							Reader in = new InputStreamReader(stream, "UTF-8");
							try {
							  int read;
							  do {
							    read = in.read(buffer, 0, buffer.length);
							    if (read>0) {
							      out.append(buffer, 0, read);
							    }
							  } while (read>=0);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
							  try {
								in.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							}
							String result = out.toString();
							
							System.out.println(result);
							
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    
					}
					//System.out.println(s.toString());
					
				}
				//System.out.println("umad?");
				
			}
		});
		*/

