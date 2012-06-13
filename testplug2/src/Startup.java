import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.*;



public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (activeWindow != null)
		{
			System.out.println("activeWindow");
			IWorkbenchPage activePage = activeWindow.getActivePage();

		    if (activePage != null)
		    {
		    	System.out.println("activePage");
		    	activePage.addPartListener(new PartListener());
		    }
		    else
		    {
		    	System.out.println("NOTactivePage");
		        activeWindow.addPageListener(new PageListener());
		    }
		}
		else
		{
			System.out.println("NOTactiveWindow");
			for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
		    {
		        for (IWorkbenchPage page : window.getPages()) {
		            if(page.getActivePart() != null) System.out.println("derp");
		            IWorkbenchPart part = page.getActivePart();
		            //System.out.println(ISaveablePart.PROP_DIRTY); //prop dirty = not saved doc
		            if(part != null) System.out.println("herp");
		            if(part instanceof IEditorPart) System.out.println("editor activated");
		            
		            
		            
		            //TextEditor editor = (TextEditor)part;
		            
		            ITextEditor editor = (ITextEditor)part;
		            IDocumentProvider dp = editor.getDocumentProvider();
		            IDocument doc = dp.getDocument(editor.getEditorInput());
		            String s = doc.get();
		            
		            System.out.println(s);
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

